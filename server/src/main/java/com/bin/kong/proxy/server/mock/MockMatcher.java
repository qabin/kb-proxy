package com.bin.kong.proxy.server.mock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bin.kong.proxy.model.mock.entity.MockProxy;
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

    public DefaultFullHttpResponse getResponseByUrl(String url, Integer port) {
        MockProxy MockProxy = mockProxyCache.get(url, port);
        if (null != MockProxy) {
            DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
            String headers = MockProxy.getHeaders();
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
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(MockProxy.getResponse() != null ? MockProxy.getResponse() : "", CharsetUtil.UTF_8),
                    httpHeaders,
                    httpHeaders
            );
        }
        return null;
    }
}
