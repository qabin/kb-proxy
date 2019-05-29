package com.bin.kong.proxy.server.runner;

import com.bin.kong.proxy.server.mock.MockProxyCache;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Order(1)
public class MockProxyCacheRunner implements ApplicationRunner {
    @Resource
    private MockProxyCache mockProxyCache;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mockProxyCache.init();
    }
}
