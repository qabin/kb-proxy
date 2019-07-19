package com.bin.kong.proxy.server.mock;

import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.dao.mapper.join.MockProxyJoinUserInfoMapper;
import com.bin.kong.proxy.model.join.entity.MockProxyJoinUserInfo;
import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.model.mock.search.MockProxySearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MockProxyCache {
    @Resource
    private MockProxyJoinUserInfoMapper mockProxyJoinUserInfoMapper;

    /**
     * 初始化加载 所有Mock 配置
     */
    public void init() {
        try {
            List<MockProxyJoinUserInfo> MockProxySearchList = mockProxyJoinUserInfoMapper.searchList(MockProxySearch.builder()
                    .build());

            for (MockProxyJoinUserInfo mockProxyJoinUserInfo : MockProxySearchList) {
                LocalCacheUtils.putIfAbsent(getCacheKey(mockProxyJoinUserInfo, mockProxyJoinUserInfo.getPort()), mockProxyJoinUserInfo, 100000, TimeUnit.DAYS);

            }
        } catch (Exception e) {
            log.error("初始化加载Mock配置异常"+e);
        }
    }

    /**
     * 根据url和port生成 缓存key
     *
     * @param url
     * @param port
     * @return
     */
    private String getCacheKey(String url, Integer port) {
        return url + "__" + port;
    }

    /**
     * 添加缓存
     *
     * @param key
     * @param MockProxy
     */
    public void put(String key, MockProxy MockProxy) {
        LocalCacheUtils.put(key, MockProxy, 100000, TimeUnit.DAYS);
    }

    /**
     * 删除缓存
     *
     * @param url
     * @param port
     */
    public void remove(String url, Integer port) {
        LocalCacheUtils.remove(getCacheKey(url, port));
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public void remove(String key) {
        LocalCacheUtils.remove(key);
    }

    /**
     * 获取配置
     *
     * @param url
     * @param port
     * @return
     */
    public MockProxyJoinUserInfo get(String url, Integer port) {
        return LocalCacheUtils.get(getCacheKey(url, port));
    }

    /**
     * 获取缓存KEY
     *
     * @param mockProxyJoinUserInfo
     * @param port
     * @return
     */
    public String getCacheKey(MockProxyJoinUserInfo mockProxyJoinUserInfo, Integer port) {
        String url = mockProxyJoinUserInfo.getUrl();
        if (mockProxyJoinUserInfo.getOnly_uri() == 1 && url.indexOf("?") != -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        return getCacheKey(url, port);
    }

    /**
     * 获取缓存KEY
     *
     * @param url
     * @param only_uri
     * @param port
     * @return
     */
    public String getCacheKey(String url, Integer only_uri, Integer port) {
        if (only_uri == 1 && url.indexOf("?") != -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        return getCacheKey(url, port);
    }
}
