package com.bin.kong.proxy.server.controller.utils;

import com.alibaba.fastjson.JSON;
import com.bin.kong.proxy.contract.request.utils.RequestByServerRequest;
import com.bin.kong.proxy.core.utils.HttpUtils;
import com.bin.kong.proxy.dao.mapper.repeater.RepeaterRequestHistoryMapper;
import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestHistory;
import com.bin.kong.proxy.server.controller.BaseController;
import com.bin.kong.proxy.server.host.HostCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
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
public class RequestUtilsController extends BaseController {
    @Resource
    private RepeaterRequestHistoryMapper repeaterRequestHistoryMapper;
    @Resource
    private HostCache hostCache;

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
                    .user_id(super.getUserInfo().getId())
                    .create_time(new Date())
                    .update_time(new Date())
                    .build();
            repeaterRequestHistoryMapper.insertSelective(repeaterRequestHistory);
            Map<String, String> hostMap = hostCache.get(super.getUserInfo().getId());
            String url = request.getUrl();
            Map<String, String> headerMap = request.getHeader() == null ? new HashMap<>() : request.getHeader();
            if (null != hostMap) {
                String domain = getDomainByUrl(request.getUrl());
                String ip = hostMap.get(domain);
                if (StringUtils.isNotEmpty(domain) && StringUtils.isNotEmpty(ip)) {
                    url =url.replace(domain, ip);
                    headerMap.put(HttpHeaders.HOST, domain);
                }
            }

            if (request.getMethod().equals(HttpMethod.GET.name())) {
                result = HttpUtils.doGet(url, null, headerMap);
            } else if (request.getMethod().equals(HttpMethod.POST.name()) || request.getMethod().equals(HttpMethod.PUT.name()) || request.getMethod().equals(HttpMethod.PATCH.name())) {
                if (null != request.getBody_type() && request.getBody_type().equals("form")) {
                    if (headerMap.get(HttpHeaders.CONTENT_TYPE) == null || !headerMap.get(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                    }
                }

                if (request.getMethod().equals(HttpMethod.POST.name())) {
                    result = HttpUtils.doPost(url, request.getRequest_form(), headerMap, request.getRequest_json());
                } else if (request.getMethod().equals(HttpMethod.PUT.name())) {
                    result = HttpUtils.doPut(url, request.getRequest_form(), headerMap, request.getRequest_json());
                } else if (request.getMethod().equals(HttpMethod.PATCH.name())) {
                    result = HttpUtils.doPatch(url, request.getRequest_form(), headerMap, request.getRequest_json());
                }
            } else if (request.getMethod().equals(HttpMethod.DELETE.name())) {
                result = HttpUtils.doDelete(url, headerMap);
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

    /**
     * 从url中获取domain
     *
     * @param url
     * @return
     */
    private static String getDomainByUrl(String url) {
        String domain;
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        }
        int maoHaoIndex = 0;
        int xieGangIndex = 0;
        if (url.indexOf(":") != -1) {
            maoHaoIndex = url.indexOf(":");
        }
        if (url.indexOf("/") != -1) {
            xieGangIndex = url.indexOf("/");
        }
        domain = url.substring(0, (maoHaoIndex < xieGangIndex ? maoHaoIndex : xieGangIndex) > 0 ? (maoHaoIndex < xieGangIndex ? maoHaoIndex : xieGangIndex) : url.length());

        return domain;
    }

}
