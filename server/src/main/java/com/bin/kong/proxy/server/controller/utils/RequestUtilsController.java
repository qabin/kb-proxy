package com.bin.kong.proxy.server.controller.utils;

import com.alibaba.fastjson.JSON;
import com.bin.kong.proxy.contract.request.utils.RequestByServerRequest;
import com.bin.kong.proxy.core.utils.HttpUtils;
import com.bin.kong.proxy.dao.mapper.repeater.RepeaterRequestHistoryMapper;
import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class RequestUtilsController {
    @Resource
    private RepeaterRequestHistoryMapper repeaterRequestHistoryMapper;

    /**
     * 使用服务器处理前端请求，解决跨域问题
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/request/by/server", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public Object requestByServer(@RequestBody RequestByServerRequest request) {
        try {
            Object result = null;
            RepeaterRequestHistory repeaterRequestHistory = RepeaterRequestHistory.builder()
                    .url(request.getUrl())
                    .method(request.getMethod())
                    .body_type(request.getBody_type())
                    .headers(JSON.toJSONString(request.getHeader()))
                    .request_form(JSON.toJSONString(request.getRequest_form()))
                    .request_json(request.getRequest_json())
                    .user_id(1)
                    .create_time(new Date())
                    .update_time(new Date())
                    .build();
            repeaterRequestHistoryMapper.insertSelective(repeaterRequestHistory);
            if (request.getMethod().equals(HttpMethod.GET.name())) {
                result = HttpUtils.doGet(request.getUrl(), null, request.getHeader());
            } else if (request.getMethod().equals(HttpMethod.POST.name()) || request.getMethod().equals(HttpMethod.PUT.name()) || request.getMethod().equals(HttpMethod.PATCH.name())) {
                Map<String, String> headerMap = new HashMap<>();
                if (request.getBody_type().equals("form")) {
                    if (null != request.getHeader() && request.getHeader().get("Content-Type") != null && request.getHeader().get("Content-Type").contains("x-www-form-urlencoded")) {
                        headerMap = request.getHeader();
                    } else if (null != request.getHeader()) {
                        headerMap = request.getHeader();
                        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
                    } else {
                        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
                    }
                } else {
                    headerMap = request.getHeader();
                }
                if (request.getMethod().equals(HttpMethod.POST.name())) {
                    result = HttpUtils.doPost(request.getUrl(), request.getRequest_form(), headerMap, request.getRequest_json());
                } else if (request.getMethod().equals(HttpMethod.PUT.name())) {
                    result = HttpUtils.doPut(request.getUrl(), request.getRequest_form(), headerMap, request.getRequest_json());
                } else if (request.getMethod().equals(HttpMethod.PATCH.name())) {
                    result = HttpUtils.doPatch(request.getUrl(), request.getRequest_form(), headerMap, request.getRequest_json());
                }
            } else if (request.getMethod().equals(HttpMethod.DELETE.name())) {
                result = HttpUtils.doDelete(request.getUrl(), request.getHeader());
            }

            repeaterRequestHistoryMapper.updateByPrimaryKeySelective(RepeaterRequestHistory.builder()
                    .id(repeaterRequestHistory.getId())
                    .update_time(new Date())
                    .response_body(((Map<String, String>) result).get("response"))
                    .response_headers(JSON.toJSONString(((Map<String, String>) result).get("headers")))
                    .build());
            return result;
        } catch (Exception e) {
            log.error("用服务器请求接口处理跨域问题异常：" + e);
        }
        return null;
    }

}
