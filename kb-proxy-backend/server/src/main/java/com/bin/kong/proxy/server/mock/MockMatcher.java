package com.bin.kong.proxy.server.mock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.server.encryption.IResponseEncrypt;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class MockMatcher {
    @Resource
    private MockProxyCache mockProxyCache;
    @Resource
    private IResponseEncrypt responseEncrypt;

    public DefaultFullHttpResponse getResponseByUrl(String url, Integer port) {
        MockProxy mockProxy = mockProxyCache.get(url, port);
        return getResponse(mockProxy);
    }

    public DefaultFullHttpResponse getResponse(MockProxy mockProxy) {
        if (null != mockProxy) {
            DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
            String headers = mockProxy.getHeaders();
            if (StringUtils.isNotEmpty(headers)) {
                try {
                    JSONObject headerOb = JSON.parseObject(headers);
                    for (Map.Entry<String, Object> stringObjectEntry : headerOb.entrySet()) {
                        if (StringUtils.isNotEmpty(stringObjectEntry.getKey()))
                            httpHeaders.add(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                    }
                } catch (Exception e) {
                    log.error("headers非标准结构：" + e);
                }
            }
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(mockProxy.getCode()),
                    Unpooled.copiedBuffer(mockProxy.getResponse() != null ? responseEncrypt.encrypt(mockProxy.getDomain(), mockProxy.getResponse()) : "", CharsetUtil.UTF_8),
                    httpHeaders,
                    httpHeaders
            );
        }
        return null;
    }
}
