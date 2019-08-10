package com.bin.kong.proxy.server.controller.user;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.contract.request.user.RegisterRequest;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.user.UserInfoMapper;
import com.bin.kong.proxy.model.user.entity.UserInfo;
import com.bin.kong.proxy.server.littleproxy.LittleProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@Slf4j
public class RegisterController {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private LittleProxyServer proxyServer;

    @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_register(@RequestBody RegisterRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            UserInfo oldUserInfo = userInfoMapper.selectByLoginName(request.getLogin_name());
            if (null != oldUserInfo) {
                response.setStatus(ResponseConstants.FAIL_CODE);
                response.setMessage("账号已存在！");
                return response;

            }

            if (StringUtils.isNotEmpty(request.getLogin_name()) && StringUtils.isNotEmpty(request.getLogin_pwd())) {
                UserInfo userInfo = UserInfo.builder()
                        .create_time(new Date())
                        .login_name(request.getLogin_name().trim())
                        .login_pwd(DigestUtils
                                .md5DigestAsHex(request.getLogin_pwd().trim().getBytes()))
                        .nick_name(request.getNick_name().trim())
                        .update_time(new Date())
                        .build();
                userInfoMapper.insertSelective(userInfo);
                proxyServer.startProxy(userInfo.getId());
                response.setStatus(ResponseConstants.SUCCESS_CODE);
            }
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行ajax_register异常：" + e);
        }

        return response;
    }
}
