package com.bin.kong.proxy.contract.request.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserInfoRequest {
    private String nick_name;
    private Integer port;
    private String new_pwd;
    private String old_pwd;
}
