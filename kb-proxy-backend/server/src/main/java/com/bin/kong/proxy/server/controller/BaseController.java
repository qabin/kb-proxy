package com.bin.kong.proxy.server.controller;

import com.bin.kong.proxy.core.constants.UserInfoConstants;
import com.bin.kong.proxy.model.user.entity.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    protected UserInfo getUserInfo() {
        return (UserInfo) SecurityUtils.getSubject().getSession().getAttribute(UserInfoConstants.CURRENT_USER);
    }

    protected Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }
}
