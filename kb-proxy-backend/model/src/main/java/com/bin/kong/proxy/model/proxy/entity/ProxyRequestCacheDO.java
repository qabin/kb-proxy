package com.bin.kong.proxy.model.proxy.entity;

import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.model.repeater.enums.MockStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProxyRequestCacheDO {
    private Integer request_id;
    @Builder.Default
    private Integer mock = MockStatusEnum.NO_MOCK.getCode();

    private MockProxy mockProxy;

}
