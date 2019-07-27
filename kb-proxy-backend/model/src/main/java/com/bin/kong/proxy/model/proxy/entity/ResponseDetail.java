package com.bin.kong.proxy.model.proxy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDetail {
    private Integer id;

    private Integer request_detail_id;

    private String code;

    private Date create_time;

    private Date update_time;

    private String header;

    private String body;

    private Integer proxy_port;
}
