package com.bin.kong.proxy.model.proxy.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSearch {
    private Integer max_id;
    private String kw;
    private Integer proxy_port;
}
