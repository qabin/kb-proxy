package com.bin.kong.proxy.server.littleproxy;

import com.bin.kong.proxy.core.utils.IpUtils;
import com.bin.kong.proxy.dao.mapper.user.UserInfoMapper;
import com.bin.kong.proxy.model.user.entity.UserInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LittleProxyServer {
    @Resource
    private ClientToProxyRequestFilter clientToProxyRequestFilter;
    @Resource
    private ServerToProxyResponseFilter serverToProxyResponseFilter;
    @Resource
    private ProxyToClientResponseFilter proxyToClientResponseFilter;
    @Resource
    private ProxyToServerResolutionStartedFilter proxyToServerResolutionStartedFilter;
    @Resource
    private ProxyServerCache proxyServerCache;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Bean
    public void run() {
        start();
    }

    public void start() {
        List<UserInfo> userInfoList = userInfoMapper.selectList(UserInfo.builder().build());

        if (CollectionUtils.isEmpty(userInfoList))
            userInfoList = new ArrayList<>();

        userInfoList.forEach(p -> {
            startProxy(p.getId());
        });

    }

    /**
     * 开启代理服务
     *
     * @param port
     */
    public void startProxy(Integer port) {
        try {
            if (proxyServerCache.get(port) == null && null == proxyServerCache.get(port)) {
                HttpProxyServer httpProxyServer = DefaultHttpProxyServer.bootstrap()
                        .withAddress(new InetSocketAddress(IpUtils.getLocalHostLANAddress().getHostAddress(), port))
                        .withManInTheMiddle(new CertificateSniffingMitmManager())
                        .withServerResolver(PersonalHostResolver.builder()
                                .userPort(port)
                                .build())
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

                                    @Override
                                    public HttpObject proxyToClientResponse(HttpObject httpObject) {
                                        return proxyToClientResponseFilter.proxyToClientResponse(httpObject, originalRequest);
                                    }

                                    @Override
                                    public InetSocketAddress proxyToServerResolutionStarted(String hostAndPort) {
                                        return proxyToServerResolutionStartedFilter.dnsResolve(hostAndPort, port);
                                    }
                                };
                            }
                        })
                        .start();

                proxyServerCache.put(port, httpProxyServer);
            }

        } catch (Exception e) {
            log.error("执行startProxy异常：" + e);
        }
    }

    /**
     * 停止代理服务
     *
     * @param port
     */
    public void stopProxy(Integer port) {
        try {
            HttpProxyServer httpProxyServer = proxyServerCache.get(port);
            if (httpProxyServer != null) {
                httpProxyServer.stop();
                proxyServerCache.remove(port);
            }

        } catch (Exception e) {
            log.error("执行startProxy异常：" + e);
        }
    }

    /**
     * 重启代理
     *
     * @param port
     */
    public void restartProxy(Integer port) {
        try {
            stopProxy(port);
            startProxy(port);

        } catch (Exception e) {
            log.error("执行startProxy异常：" + e);
        }
    }
}
