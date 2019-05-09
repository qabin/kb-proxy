package com.bin.kong.proxy.model.proxy.search;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestSearch {
    private Integer max_id;
    private String kw;
}
