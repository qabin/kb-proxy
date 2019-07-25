package com.bin.kong.proxy.model.repeater.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepeaterRequestHistory {
    private Integer id;
    private String url;
    private String method;
    private String headers;
    private String request_json;
    private String request_form;
    private String body_type;
    private String response_headers;
    private String response_body;
    private Integer user_id;
    private Date create_time;
    private Date update_time;
}
