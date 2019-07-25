package com.bin.kong.proxy.contract.request.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String login_name;
    private String login_pwd;
    private String nick_name;
}
