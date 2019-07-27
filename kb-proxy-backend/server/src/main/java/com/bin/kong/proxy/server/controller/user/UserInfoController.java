package com.bin.kong.proxy.server.controller.user;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.contract.request.user.UpdateUserInfoRequest;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.core.constants.UserInfoConstants;
import com.bin.kong.proxy.core.utils.IpUtils;
import com.bin.kong.proxy.dao.mapper.user.UserInfoMapper;
import com.bin.kong.proxy.model.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@Slf4j
public class UserInfoController {
    @Resource
    private UserInfoMapper userInfoMapper;

    @RequestMapping(value = "/user/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_get_user_info() {
        GenericResponse response = new GenericResponse();
        try {
            Session session = SecurityUtils.getSubject().getSession();

            UserInfo userInfo = (UserInfo) session.getAttribute(UserInfoConstants.CURRENT_USER);

            if (userInfo != null && userInfo.getId() != null) {
                if (!ObjectUtils.isEmpty(IpUtils.getLocalHostLANAddress()) && StringUtils.isNotEmpty(IpUtils.getLocalHostLANAddress().getHostAddress())) {
                    userInfo.setIp(IpUtils.getLocalHostLANAddress().getHostAddress());
                }
                response.setData(userInfo);
                response.setStatus(ResponseConstants.SUCCESS_CODE);
                response.setMessage("获取用户信息成功");
            } else {
                response.setStatus(ResponseConstants.STATUS_UNLOGIN);
                response.setMessage("未获取到登录信息");
            }
        } catch (InvalidSessionException e) {
            log.error("执行ajax_get_user_info异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/user/info", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_update_user_info(@RequestBody UpdateUserInfoRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            Session session = SecurityUtils.getSubject().getSession();

            UserInfo userInfo = (UserInfo) session.getAttribute(UserInfoConstants.CURRENT_USER);

            UserInfo dbUserInfo = userInfoMapper.selectByLoginName(userInfo.getLogin_name());
            if (dbUserInfo != null && dbUserInfo.getId() != null) {

                if (request.getNew_pwd() != null && !(DigestUtils
                        .md5DigestAsHex(request.getOld_pwd().trim().getBytes()).equals(dbUserInfo.getLogin_pwd()))) {
                    response.setStatus(ResponseConstants.FAIL_CODE);
                    response.setMessage("密码不正确！");
                    return response;
                }

                userInfoMapper.updateByPrimaryKeySelective(UserInfo.builder()
                        .id(dbUserInfo.getId())
                        .nick_name(request.getNick_name())
                        .login_pwd(request.getNew_pwd() != null ? DigestUtils
                                .md5DigestAsHex(request.getNew_pwd().trim().getBytes()) : null)
                        .update_time(new Date())
                        .build());

                if (null != request.getNick_name()) {
                    userInfo.setNick_name(request.getNick_name());
                }
                if (null != request.getNew_pwd()) {
                    userInfo.setLogin_pwd(DigestUtils
                            .md5DigestAsHex(request.getNew_pwd().trim().getBytes()));
                }
                session.setAttribute(UserInfoConstants.CURRENT_USER, userInfo);

                response.setStatus(ResponseConstants.SUCCESS_CODE);
                response.setMessage("更新成功！");
            } else {
                response.setStatus(ResponseConstants.STATUS_UNLOGIN);
                response.setMessage("更新失败！");
            }
        } catch (InvalidSessionException e) {
            log.error("执行ajax_update_user_info异常：" + e);
        }
        return response;
    }
}
