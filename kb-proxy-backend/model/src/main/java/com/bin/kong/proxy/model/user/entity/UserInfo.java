package com.bin.kong.proxy.model.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Integer id;

    private String login_name;

    private String login_pwd;

    private String nick_name;

    private Date create_time;

    private Date update_time;

    private Integer status;

    private String ip;
}
