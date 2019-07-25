package com.bin.kong.proxy.model.mock.search;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MockProxyHistorySearch {
    private Integer mock_id;
    private String kw;
    @Builder.Default
    private Integer size = 10;
    @Builder.Default
    private Integer page = 0;

    private Integer startNum;

    public Integer getStartNum() {
        if (page > 0) {
            return size * page;
        } else {
            return 0;
        }
    }
}
