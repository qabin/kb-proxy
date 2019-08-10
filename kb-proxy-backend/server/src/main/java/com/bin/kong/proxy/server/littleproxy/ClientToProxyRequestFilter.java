package com.bin.kong.proxy.server.littleproxy;

import com.alibaba.fastjson.JSON;
import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.core.constants.ProxyConstants;
import com.bin.kong.proxy.core.utils.IpUtils;
import com.bin.kong.proxy.dao.mapper.mock.MockProxyHistoryMapper;
import com.bin.kong.proxy.dao.mapper.proxy.RequestDetailMapper;
import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.model.mock.entity.MockProxyHistory;
import com.bin.kong.proxy.model.proxy.entity.ProxyRequestCacheDO;
import com.bin.kong.proxy.model.proxy.entity.RequestDetail;
import com.bin.kong.proxy.model.repeater.enums.MockStatusEnum;
import com.bin.kong.proxy.server.mock.MockProxyCache;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class ClientToProxyRequestFilter {
    @Resource
    private RequestDetailMapper requestDetailMapper;
    @Resource
    private MockProxyHistoryMapper mockProxyHistoryMapper;
    @Resource
    private MockProxyCache mockProxyCache;
    @Value("${proxy.excludeDomain:#{null}}")
    private String excludeDomain;


    private static Map<Object, HttpEntity> requestMap = new HashMap<>();

    public HttpResponse clientToProxyRequest(HttpObject httpObject, HttpRequest originalRequest, Integer port) {
        try {

            /**
             * 暂时这么处理，这会影响到Https请求的显示 ，不过目前看来不影响 https请求的发送
             */
            if (originalRequest.getUri().startsWith("/") && httpObject instanceof HttpRequest&& ((HttpRequest) httpObject).headers().contains(ProxyConstants.KB_PROXY_FROM_NGINX)) {
                if (httpObject instanceof DefaultHttpRequest) {
                    ((DefaultHttpRequest) httpObject).setUri("http://" + originalRequest.headers().get(HttpHeaders.HOST) + originalRequest.getUri());
                }
                originalRequest.setUri(originalRequest.headers().get(HttpHeaders.HOST) + originalRequest.getUri());
            }


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
                        .ip(getIp(originalRequest.headers().get(HttpHeaders.HOST), port))
                        .build();

                myRequest.setHttpHeaders(((HttpRequest) httpObject).headers());
                //排除CONNECT请求
                if (!originalRequest.method().name().equals(HttpMethod.CONNECT.name())) {
                    if (!isExclude(requestDetail.getHost())) {
                        requestMap.put(originalRequest, myRequest);
                        requestDetailMapper.insertSelective(requestDetail);

                        // 保存Mock 记录
                        String url = getUrl(originalRequest.uri(), originalRequest.headers().get(HttpHeaders.HOST), getPath(originalRequest.uri(), originalRequest.headers().get(HttpHeaders.HOST)));
                        MockProxy mockProxy = mockProxyCache.get(url, port);
                        if (null == mockProxy && url.indexOf("?") != -1) {
                            String uri = url.substring(0, url.indexOf("?"));
                            mockProxy = mockProxyCache.get(uri, port);
                        }
                        if (mockProxy != null) {
                            requestDetailMapper.updateByPrimaryKeySelective(RequestDetail.builder()
                                    .mock(MockStatusEnum.IS_MOCK.getCode())
                                    .id(requestDetail.getId())
                                    .build());

                            LocalCacheUtils.putIfAbsent(originalRequest, ProxyRequestCacheDO.builder()
                                    .mock(MockStatusEnum.IS_MOCK.getCode())
                                    .request_id(requestDetail.getId())
                                    .mockProxy(mockProxy)
                                    .build(), 1, TimeUnit.DAYS);

                            mockProxyHistoryMapper.insertSelective(MockProxyHistory.builder()
                                    .mock_id(mockProxy.getId())
                                    .code(mockProxy.getCode())
                                    .create_time(new Date())
                                    .headers(mockProxy.getHeaders())
                                    .response(mockProxy.getResponse())
                                    .method(mockProxy.getMethod())
                                    .url(url)
                                    .build());

                        } else {
                            //添加缓存
                            LocalCacheUtils.putIfAbsent(originalRequest, ProxyRequestCacheDO.builder()
                                    .request_id(requestDetail.getId())
                                    .mock(MockStatusEnum.NO_MOCK.getCode())
                                    .build(), 1, TimeUnit.DAYS);
                        }

                    }
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
                    requestDetail.setId(((ProxyRequestCacheDO) LocalCacheUtils.get(originalRequest)).getRequest_id());
                    requestDetail.setUpdate_time(new Date());
                    requestDetailMapper.updateByPrimaryKeySelective(requestDetail);
                    requestMap.remove(originalRequest);
                }

                if (originalRequest.method().name().equals(HttpMethod.CONNECT.name())) {
                    return null;
                }

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

    /**
     * 获取排除的站点列表
     *
     * @return
     */
    private List<String> getExcluedeDomain() {
        List<String> excludeList = new ArrayList<>();
        excludeList.add(IpUtils.getLocalHostLANAddress().getHostAddress());
        if (StringUtils.isNotEmpty(excludeDomain)) {
            List<String> excludeDomainList = Arrays.asList(excludeDomain.split(","));
            excludeList.addAll(excludeDomainList);
        }
        return excludeList;
    }

    /**
     * 是否排除站点
     *
     * @param domain
     * @returnde
     */
    private Boolean isExclude(String domain) {

        if (StringUtils.isNotEmpty(domain)) {
            if (domain.indexOf(":") != -1) {
                domain = domain.substring(0, domain.indexOf(":"));
            }
            if (getExcluedeDomain().contains(domain)) {
                return true;
            }
        }

        return false;
    }

    private String getIp(String domain, Integer port) {

        if (StringUtils.isNotEmpty(domain)) {
            if (domain.indexOf(":") != -1) {
                domain = domain.substring(0, domain.indexOf(":"));
            }

            return PersonalHostResolver.builder().userPort(port).build().resolve(domain).getHostAddress();
        }
        return null;
    }
}
