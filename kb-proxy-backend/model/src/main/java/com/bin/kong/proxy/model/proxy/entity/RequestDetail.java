package com.bin.kong.proxy.model.proxy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetail {
    private Integer id;

    private String method;

    private String code;

    private String host;

    private String path;

    private String protocol;

    private String header;

    private String body;

    private String mime_type;

    private Date create_time;

    private Date update_time;

    private String url;

    private Integer proxy_port;

    private Integer mock;

    private String ip;
}
