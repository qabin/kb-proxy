package com.bin.kong.proxy.model.mock.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockProxyHistory {
    private Integer id;

    private Integer mock_id;

    private String method;

    private Date create_time;

    private Integer code;

    private String url;

    private String response;

    private String headers;
}
