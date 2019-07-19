package com.bin.kong.proxy.server.controller.proxy;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.core.constants.UserInfoConstants;
import com.bin.kong.proxy.dao.mapper.user.UserInfoMapper;
import com.bin.kong.proxy.model.proxy.enums.UserInfoStatusEnum;
import com.bin.kong.proxy.model.user.entity.UserInfo;
import com.bin.kong.proxy.server.controller.BaseController;
import com.bin.kong.proxy.server.littleproxy.LittleProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class ProxyController extends BaseController {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private LittleProxyServer proxyServer;

    /**
     * 停止代理
     *
     * @return
     */
    @RequestMapping(value = "/proxy/_stop", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse proxy_stop() {
        GenericResponse response = new GenericResponse();
        try {
            proxyServer.stopProxy(super.getUserInfo().getId());
            userInfoMapper.updateByPrimaryKeySelective(UserInfo.builder()
                    .id(super.getUserInfo().getId())
                    .status(UserInfoStatusEnum.STOP.getCode())
                    .build());
            UserInfo userInfo = super.getUserInfo();
            userInfo.setStatus(UserInfoStatusEnum.STOP.getCode());
            Session session = super.getSession();
            session.setAttribute(UserInfoConstants.CURRENT_USER, userInfo);

            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行proxy_stop异常：" + e);
        }
        return response;
    }


    /**
     * 开启代理
     *
     * @return
     */
    @RequestMapping(value = "/proxy/_start", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse proxy_start() {
        GenericResponse response = new GenericResponse();
        try {
            proxyServer.startProxy(super.getUserInfo().getId());
            userInfoMapper.updateByPrimaryKeySelective(UserInfo.builder()
                    .id(super.getUserInfo().getId())
                    .status(UserInfoStatusEnum.START.getCode())
                    .build());
            UserInfo userInfo = super.getUserInfo();
            userInfo.setStatus(UserInfoStatusEnum.START.getCode());
            Session session = super.getSession();
            session.setAttribute(UserInfoConstants.CURRENT_USER, userInfo);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行proxy_start异常：" + e);
        }
        return response;
    }
}
