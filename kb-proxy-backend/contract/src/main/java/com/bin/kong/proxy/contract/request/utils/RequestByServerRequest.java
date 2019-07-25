package com.bin.kong.proxy.contract.request.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestByServerRequest {
    private String url;
    private String method;
    private Map<String, String> header;
    private Map<String, String> request_form;
    private String request_json;
    private String body_type;
}
