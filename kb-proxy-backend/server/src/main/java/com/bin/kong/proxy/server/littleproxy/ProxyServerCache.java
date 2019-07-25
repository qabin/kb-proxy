package com.bin.kong.proxy.server.littleproxy;

import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.littleshoot.proxy.HttpProxyServer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProxyServerCache {
    private static final String PROXY_SERVER_START_WITH = "PROXY_SERVER_";


    private String getCacheKey(Integer port) {
        return PROXY_SERVER_START_WITH + "__" + port;
    }

    public void put(Integer port, HttpProxyServer httpProxyServer) {
        LocalCacheUtils.put(getCacheKey(port), httpProxyServer, 100000, TimeUnit.DAYS);
    }


    public void remove(Integer port) {
        LocalCacheUtils.remove(getCacheKey(port));
    }

    public HttpProxyServer get(Integer port) {
        return LocalCacheUtils.get(getCacheKey(port));
    }

}
