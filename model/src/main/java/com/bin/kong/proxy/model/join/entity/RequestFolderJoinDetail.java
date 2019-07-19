package com.bin.kong.proxy.model.join.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestFolderJoinDetail {
    private Integer id;

    private String name;

    private String description;

    private String url;

    private String method;

    private String headers;

    private String body_type;

    private Integer user_id;

    private String request_json;

    private String request_form;

    private String folder_name;

    private Integer folder_id;

    private Date create_time;

    private Date update_time;
}
