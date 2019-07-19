package com.bin.kong.proxy.server.host;

import com.bin.kong.proxy.core.cache.impl.LocalCacheUtils;
import com.bin.kong.proxy.core.utils.HostUtils;
import com.bin.kong.proxy.dao.mapper.env.EnvInfoMapper;
import com.bin.kong.proxy.model.env.entity.EnvInfo;
import com.bin.kong.proxy.model.env.enums.EnvStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HostCache {
    @Resource
    private EnvInfoMapper envInfoMapper;
    private static final String HOST_START_WITH = "HOST_";

    /**
     * 初始化加载 所有Host 配置
     */
    public void init() {
        try {
            List<EnvInfo> envInfoList = envInfoMapper.selectList(EnvInfo.builder()
                    .status(EnvStatusEnum.START.getCode())
                    .build());
            for (EnvInfo envInfo : envInfoList) {
                LocalCacheUtils.putIfAbsent(getCacheKey(envInfo.getUser_id()), HostUtils.getHostMapFromString(envInfo.getHosts()), 100000, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.error("初始化加载HOST异常" + e);
        }
    }

    /**
     * 根据port生成 缓存key
     *
     * @param port
     * @return
     */
    private String getCacheKey(Integer port) {
        return HOST_START_WITH + "__" + port;
    }

    /**
     * 添加缓存
     *
     * @param port
     * @param hostMap
     */
    public void put(Integer port, Map<String, String> hostMap) {
        LocalCacheUtils.put(getCacheKey(port), hostMap, 100000, TimeUnit.DAYS);
    }

    /**
     * 删除缓存
     *
     * @param port
     */
    public void remove(Integer port) {
        LocalCacheUtils.remove(getCacheKey(port));
    }

    /**
     * 获取HOST
     *
     * @param port
     * @return
     */
    public Map<String, String> get(Integer port) {
        return LocalCacheUtils.get(getCacheKey(port));
    }

}
