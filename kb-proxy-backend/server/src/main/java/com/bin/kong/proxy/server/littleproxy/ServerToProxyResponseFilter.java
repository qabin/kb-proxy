package com.bin.kong.proxy.server.littleproxy;

import com.alibaba.fastjson.JSON;
import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.dao.mapper.proxy.RequestDetailMapper;
import com.bin.kong.proxy.dao.mapper.proxy.ResponseDetailMapper;
import com.bin.kong.proxy.model.proxy.entity.ProxyRequestCacheDO;
import com.bin.kong.proxy.model.proxy.entity.RequestDetail;
import com.bin.kong.proxy.model.proxy.entity.ResponseDetail;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class ServerToProxyResponseFilter {
    @Resource
    private ResponseDetailMapper responseDetailMapper;
    @Resource
    private RequestDetailMapper requestDetailMapper;

    private static Map<Object, HttpEntity> responseMap = new HashMap<>();

    public HttpObject serverToProxyResponse(HttpObject httpObject, HttpRequest originalRequest,Integer port) {
        try {

            if (LocalCacheUtils.get(originalRequest) == null) {
                return httpObject;
            }

            HttpEntity myResponse = responseMap.get(originalRequest) == null ? HttpEntity.builder().build() : responseMap.get(originalRequest);

            if (httpObject instanceof HttpResponse) {
                myResponse.setHttpHeaders(((HttpResponse) httpObject).headers());
                myResponse.setCode(((HttpResponse) httpObject).status());
                if (!originalRequest.method().name().equals(HttpMethod.CONNECT.name())) {
                    requestDetailMapper.updateByPrimaryKeySelective(RequestDetail.builder()
                            .id(((ProxyRequestCacheDO) LocalCacheUtils.get(originalRequest)).getRequest_id())
                            .code(getHttpCode(myResponse.getCode().toString()))
                            .mime_type(((HttpResponse) httpObject).headers().get(HttpHeaders.Names.CONTENT_TYPE))
                            .update_time(new Date())
                            .build());
                    responseMap.put(originalRequest, myResponse);
                }

            } else if (httpObject instanceof DefaultHttpContent) {
                HttpHeaders httpHeaders = myResponse.getHttpHeaders();
                ProxyHttpUtils.dealHttpContent((HttpContent) httpObject, originalRequest, myResponse, httpHeaders, responseMap);
            }
            if (httpObject instanceof LastHttpContent) {
                ResponseDetail responseDetail = ResponseDetail.builder().build();

                if (myResponse.getContent() != null) {
                    HttpHeaders httpHeaders = myResponse.getHttpHeaders();
                    String ce = httpHeaders.get("Content-Encoding");
                    if (ce != null && ce.contains("gzip")) {
                        if (myResponse.getContent() != null) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(myResponse.getContent());
                            GZIPInputStream gzis = new GZIPInputStream(bais);
                            byte[] decompressedData = IOUtils.toByteArray(gzis);
                            responseDetail.setBody(new String(decompressedData, CharEncoding.UTF_8));
                        }
                    } else {
                        if (myResponse.getContent() != null) {
                            responseDetail.setBody(new String(myResponse.getContent(), CharEncoding.UTF_8));
                        }
                    }
                }
                if (LocalCacheUtils.get(originalRequest) != null) {
                    responseDetail.setRequest_detail_id(((ProxyRequestCacheDO) LocalCacheUtils.get(originalRequest)).getRequest_id());
                    responseDetail.setCode(getHttpCode(myResponse.getCode().toString()));
                    responseDetail.setHeader(JSON.toJSONString(ProxyHttpUtils.getHeaderMap(myResponse.getHttpHeaders())));
                    responseDetail.setCreate_time(new Date());
                    responseDetail.setProxy_port(port);
                    responseDetailMapper.insertSelective(responseDetail);
                    //删除缓存
                    LocalCacheUtils.remove(originalRequest);
                    responseMap.remove(originalRequest);
                }
            }

        } catch (Exception e) {
            log.error("ServerToProxyResponseFilter执行异常：" + e);
        }
        return httpObject;
    }

    /**
     * 处理请求状态码包含空格的问题
     *
     * @param code
     * @return
     */
    private String getHttpCode(String code) {
        if (code.indexOf(" ") != -1) {
            return code.substring(0, code.indexOf(" "));
        }
        return code;
    }

}
