package com.bin.kong.proxy.server.littleproxy;

import com.alibaba.fastjson.JSON;
import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.dao.mapper.mock.MockProxyHistoryMapper;
import com.bin.kong.proxy.dao.mapper.proxy.RequestDetailMapper;
import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.model.mock.entity.MockProxyHistory;
import com.bin.kong.proxy.model.proxy.entity.RequestDetail;
import com.bin.kong.proxy.server.mock.MockMatcher;
import com.bin.kong.proxy.server.mock.MockProxyCache;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class ClientToProxyRequestFilter {
    @Resource
    private RequestDetailMapper requestDetailMapper;
    @Resource
    private MockMatcher mockMatcher;
    @Resource
    private MockProxyHistoryMapper mockProxyHistoryMapper;
    @Resource
    private MockProxyCache mockProxyCache;

    private static Map<Object, HttpEntity> requestMap = new HashMap<>();

    public HttpResponse clientToProxyRequest(HttpObject httpObject, HttpRequest originalRequest, Integer port) {
        try {
            // TODO: implement your filtering here
            HttpEntity myRequest = requestMap.get(originalRequest) == null ? HttpEntity.builder().build() : requestMap.get(originalRequest);

            if (httpObject instanceof HttpRequest) {
                RequestDetail requestDetail = RequestDetail.builder()
                        .create_time(new Date())
                        .url(getUrl(originalRequest.uri(), originalRequest.headers().get(HttpHeaders.HOST), getPath(originalRequest.uri(), originalRequest.headers().get(HttpHeaders.HOST))))
                        .path(getPath(originalRequest.uri(), originalRequest.headers().get(HttpHeaders.HOST)))
                        .mime_type(getMimeType(originalRequest.headers()))
                        .protocol(originalRequest.protocolVersion().toString())
                        .method(originalRequest.method().name())
                        .header(JSON.toJSONString(ProxyHttpUtils.getHeaderMap(((HttpRequest) httpObject).headers())))
                        .host(originalRequest.headers().get(HttpHeaders.HOST))
                        .proxy_port(port)
                        .build();

                myRequest.setHttpHeaders(((HttpRequest) httpObject).headers());
                //排除CONNECT请求
                if (!originalRequest.method().name().equals(HttpMethod.CONNECT.name())) {
                    requestMap.put(originalRequest, myRequest);
                    requestDetailMapper.insertSelective(requestDetail);
                    //添加缓存
                    LocalCacheUtils.putIfAbsent(originalRequest, requestDetail.getId(), 1, TimeUnit.DAYS);
                }
            } else if (httpObject instanceof DefaultHttpContent) {
                io.netty.handler.codec.http.HttpHeaders httpHeaders = myRequest.getHttpHeaders();
                ProxyHttpUtils.dealHttpContent((HttpContent) httpObject, originalRequest, myRequest, httpHeaders, requestMap);
            }

            if (httpObject instanceof LastHttpContent) {
                RequestDetail requestDetail = RequestDetail.builder().build();

                if (myRequest.getContent() != null) {
                    io.netty.handler.codec.http.HttpHeaders httpHeaders = myRequest.getHttpHeaders();
                    String ce = httpHeaders.get(HttpHeaders.CONTENT_ENCODING);
                    if (ce != null && ce.contains("gzip")) {
                        if (myRequest.getContent() != null) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(myRequest.getContent());
                            GZIPInputStream gzis = new GZIPInputStream(bais);
                            byte[] decompressedData = IOUtils.toByteArray(gzis);
                            requestDetail.setBody(new String(decompressedData, CharEncoding.UTF_8));
                        }
                    } else {
                        if (myRequest.getContent() != null) {
                            requestDetail.setBody(new String(myRequest.getContent(), CharEncoding.UTF_8));
                        }
                    }
                }
                if (LocalCacheUtils.get(originalRequest) != null) {
                    requestDetail.setId(LocalCacheUtils.get(originalRequest));
                    requestDetail.setUpdate_time(new Date());
                    requestDetailMapper.updateByPrimaryKeySelective(requestDetail);
                    requestMap.remove(originalRequest);
                }
            }
            if (originalRequest.method().name().equals(HttpMethod.CONNECT.name())) {
                return null;
            }
            // 保存Mock 记录
            String url = getUrl(originalRequest.uri(), originalRequest.headers().get(HttpHeaders.HOST), getPath(originalRequest.uri(), originalRequest.headers().get(HttpHeaders.HOST)));
            MockProxy mockProxy = mockProxyCache.get(url, port);
            if (null == mockProxy && url.indexOf("?") != -1) {
                String uri = url.substring(0, url.indexOf("?"));
                mockProxy = mockProxyCache.get(uri, port);
            }
            if (mockProxy != null) {
                mockProxyHistoryMapper.insertSelective(MockProxyHistory.builder()
                        .mock_id(mockProxy.getId())
                        .code(mockProxy.getCode())
                        .create_time(new Date())
                        .headers(mockProxy.getHeaders())
                        .response(mockProxy.getResponse())
                        .method(mockProxy.getMethod())
                        .url(url)
                        .build());

                return mockMatcher.getResponse(mockProxy);
            }

        } catch (IOException e) {
            log.error("clientToProxyRequest执行异常：" + e);
            return null;
        }
        return null;
    }

    /**
     * 从Header中获取mime_type
     *
     * @param headers
     * @return
     */
    private String getMimeType(io.netty.handler.codec.http.HttpHeaders headers) {
        if (null != headers) {
            String mime_type = headers.get(HttpHeaders.CONTENT_TYPE);
            if (null != mime_type) {
                return mime_type;
            } else {
                mime_type = headers.get(HttpHeaders.ACCEPT);
                if (null != mime_type) {
                    if (mime_type.indexOf(",") != -1) {
                        return mime_type.substring(0, mime_type.indexOf(","));
                    } else {
                        return mime_type;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据url和host 获取path
     *
     * @param url
     * @param host
     * @return
     */
    private String getPath(String url, String host) {
        if (null != host && null != url) {
            if (url.indexOf(host) != -1) {
                return url.substring(url.indexOf(host) + host.length());
            }
        }
        return url;
    }

    /**
     * 获取Url
     *
     * @param uri
     * @param host
     * @param path
     * @return
     */
    private String getUrl(String uri, String host, String path) {
        if (uri.contains(host) && uri.contains(path)) {
            return uri;
        } else {
            return "https://" + host + path;
        }
    }
}
