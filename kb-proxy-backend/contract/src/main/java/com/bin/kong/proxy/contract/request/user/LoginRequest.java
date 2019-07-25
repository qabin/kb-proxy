package com.bin.kong.proxy.contract.request.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    private String login_name;
    private String login_pwd;
}
