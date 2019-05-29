package com.bin.kong.proxy.server.littleproxy;

import com.alibaba.fastjson.JSON;
import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.dao.mapper.proxy.RequestDetailMapper;
import com.bin.kong.proxy.dao.mapper.proxy.ResponseDetailMapper;
import com.bin.kong.proxy.model.proxy.entity.RequestDetail;
import com.bin.kong.proxy.model.proxy.entity.ResponseDetail;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ProxyToClientResponseFilter {
    @Resource
    private ResponseDetailMapper responseDetailMapper;
    @Resource
    private RequestDetailMapper requestDetailMapper;

    private static Map<Object, HttpEntity> responseMap = new HashMap<>();

    public HttpObject proxyToClientResponse(HttpObject httpObject, HttpRequest originalRequest) {
        try {
            if (!(httpObject instanceof DefaultFullHttpResponse)) {
                return httpObject;
            }

            HttpEntity myResponse = responseMap.get(originalRequest) == null ? HttpEntity.builder().build() : responseMap.get(originalRequest);
            myResponse.setHttpHeaders(((DefaultFullHttpResponse) httpObject).headers());
            myResponse.setCode(((DefaultFullHttpResponse) httpObject).status());
            if (!originalRequest.method().name().equals(HttpMethod.CONNECT.name())) {
                requestDetailMapper.updateByPrimaryKeySelective(RequestDetail.builder()
                        .id(LocalCacheUtils.get(originalRequest))
                        .code(getHttpCode(myResponse.getCode().toString()))
                        .mime_type(((DefaultFullHttpResponse) httpObject).headers().get("Content-type"))
                        .update_time(new Date())
                        .build());
                responseMap.put(originalRequest, myResponse);
            }

            HttpHeaders httpHeaders = myResponse.getHttpHeaders();
            if (httpObject instanceof HttpContent)
                ProxyHttpUtils.dealHttpContent((HttpContent) httpObject, originalRequest, myResponse, httpHeaders, responseMap);
            if (httpObject instanceof LastHttpContent) {
                ResponseDetail responseDetail = ResponseDetail.builder().build();
                if (((LastHttpContent) httpObject).content() != null) {
                    ByteBuf buf = ((LastHttpContent) httpObject).content();
                    buf.markReaderIndex();
                    byte[] array = new byte[buf.readableBytes()];
                    buf.readBytes(array);
                    buf.resetReaderIndex();
                    responseDetail.setBody(new String(array, CharsetUtil.UTF_8));
                }

                if (LocalCacheUtils.get(originalRequest) != null) {
                    responseDetail.setRequest_detail_id(LocalCacheUtils.get(originalRequest));
                    responseDetail.setCode(((DefaultFullHttpResponse) httpObject).status().toString());
                    responseDetail.setHeader(JSON.toJSONString(ProxyHttpUtils.getHeaderMap(((DefaultFullHttpResponse) httpObject).headers())));
                    responseDetail.setCreate_time(new Date());
                    responseDetail.setUpdate_time(new Date());
                    responseDetailMapper.insertSelective(responseDetail);
                    //删除缓存
                    LocalCacheUtils.remove(originalRequest);
                    responseMap.remove(originalRequest);
                }
            }

        } catch (Exception e) {
            log.error("ProxyToClientResponseFilter执行异常：" + e);
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
