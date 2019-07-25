package com.bin.kong.proxy.model.mock.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MockProxy {
    private Integer id;

    private String name;

    private String description;

    private String url;

    private String method;

    private Integer code;

    private String response;

    private String headers;

    private Integer is_used;

    private Integer user_id;

    private Date create_time;

    private Date update_time;

    private Integer only_uri;

    private String domain;

}
