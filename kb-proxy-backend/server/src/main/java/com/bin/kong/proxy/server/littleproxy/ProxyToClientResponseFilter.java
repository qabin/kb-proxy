package com.bin.kong.proxy.server.littleproxy;

import com.alibaba.fastjson.JSON;
import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.dao.mapper.proxy.RequestDetailMapper;
import com.bin.kong.proxy.dao.mapper.proxy.ResponseDetailMapper;
import com.bin.kong.proxy.model.proxy.entity.ProxyRequestCacheDO;
import com.bin.kong.proxy.model.proxy.entity.RequestDetail;
import com.bin.kong.proxy.model.proxy.entity.ResponseDetail;
import com.bin.kong.proxy.model.repeater.enums.MockStatusEnum;
import com.bin.kong.proxy.server.mock.MockMatcher;
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
    @Resource
    private MockMatcher mockMatcher;

    private static Map<Object, HttpEntity> responseMap = new HashMap<>();

    public HttpObject proxyToClientResponse(HttpObject httpObject, HttpRequest originalRequest,Integer port) {
        try {
            if (LocalCacheUtils.get(originalRequest) == null || ((ProxyRequestCacheDO) LocalCacheUtils.get(originalRequest)).getMock() != MockStatusEnum.IS_MOCK.getCode()) {
                return httpObject;
            }

            HttpEntity myResponse = responseMap.get(originalRequest) == null ? HttpEntity.builder().build() : responseMap.get(originalRequest);
            httpObject = mockMatcher.getResponse(((ProxyRequestCacheDO) LocalCacheUtils.get(originalRequest)).getMockProxy());
            myResponse.setHttpHeaders(((DefaultFullHttpResponse) httpObject).headers());
            myResponse.setCode(((DefaultFullHttpResponse) httpObject).status());
            if (!originalRequest.method().name().equals(HttpMethod.CONNECT.name())) {
                requestDetailMapper.updateByPrimaryKeySelective(RequestDetail.builder()
                        .id(((ProxyRequestCacheDO) LocalCacheUtils.get(originalRequest)).getRequest_id())
                        .code(getHttpCode(myResponse.getCode().toString()))
                        .mime_type(((DefaultFullHttpResponse) httpObject).headers().get(HttpHeaders.Names.CONTENT_TYPE))
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
                    responseDetail.setRequest_detail_id(((ProxyRequestCacheDO) LocalCacheUtils.get(originalRequest)).getRequest_id());
                    responseDetail.setCode(((DefaultFullHttpResponse) httpObject).status().toString());
                    responseDetail.setHeader(JSON.toJSONString(ProxyHttpUtils.getHeaderMap(((DefaultFullHttpResponse) httpObject).headers())));
                    responseDetail.setCreate_time(new Date());
                    responseDetail.setUpdate_time(new Date());
                    responseDetail.setProxy_port(port);
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
