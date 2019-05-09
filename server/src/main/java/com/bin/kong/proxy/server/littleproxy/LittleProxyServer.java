package com.bin.kong.proxy.server.littleproxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AttributeKey;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Component
public class LittleProxyServer {
    @Resource
    private ClientToProxyRequestFilter clientToProxyRequestFilter;
    @Resource
    private ServerToProxyResponseFilter serverToProxyResponseFilter;
    private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");


    @Bean
    public void main() {
        way1();
    }

    public void way1() {
        try {
            Integer port = 9999;
            DefaultHttpProxyServer.bootstrap()
                    .withAddress(new InetSocketAddress("172.20.132.29", port))
                    //.withPort(9090) // for both HTTP and HTTPS
                    .withManInTheMiddle(new CertificateSniffingMitmManager())
                    .withFiltersSource(new HttpFiltersSourceAdapter() {
                        public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {

                            return new HttpFiltersAdapter(originalRequest, ctx) {
                                @Override
                                public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                    return clientToProxyRequestFilter.clientToProxyRequest(httpObject, originalRequest, port);
                                }

                                @Override
                                public HttpObject serverToProxyResponse(HttpObject httpObject) {
                                    return serverToProxyResponseFilter.serverToProxyResponse(httpObject, originalRequest);
                                }

                            };
                        }
                    })
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
