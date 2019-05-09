package com.bin.kong.proxy.server.littleproxy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class ProxyHttpUtils {
    /**
     * 从HttpHeaders获取请求头Map
     *
     * @param httpHeaders
     * @return
     */
    public static HashMap<String, Object> getHeaderMap(HttpHeaders httpHeaders) {
        HashMap<String, Object> headerMap = new HashMap<>();
        if (httpHeaders != null) {
            for (Map.Entry<String, String> httpHeader : httpHeaders) {
                headerMap.put(httpHeader.getKey(), httpHeader.getValue());
            }
        }
        return headerMap;
    }

    /**
     * 处理http请求
     * @param httpObject
     * @param originalRequest
     * @param myRequest
     * @param httpHeaders
     * @param requestMap
     */
    public static void dealHttpContent(HttpContent httpObject, HttpRequest originalRequest, HttpEntity myRequest, io.netty.handler.codec.http.HttpHeaders httpHeaders, Map<Object, HttpEntity> requestMap) {
        if (httpHeaders != null) {
            String contentType = httpHeaders.get("Content-Type");
            if (contentType != null && !contentType.contains("image") && !contentType.contains("audio")
                    && !contentType.contains("zip") && !contentType.contains("application/octet-stream")
            ) {
                ByteBuf buf = httpObject.content();
                buf.markReaderIndex();
                byte[] array = new byte[buf.readableBytes()];
                buf.readBytes(array);
                buf.resetReaderIndex();
                myRequest.appendByte(array);
                requestMap.put(originalRequest, myRequest);
            }
        }
    }

}
