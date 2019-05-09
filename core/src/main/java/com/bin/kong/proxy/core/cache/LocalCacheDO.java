package com.bin.kong.proxy.core.cache;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalCacheDO {
    private long cacheTime;
    private long createTime;
    private Object value;
}
