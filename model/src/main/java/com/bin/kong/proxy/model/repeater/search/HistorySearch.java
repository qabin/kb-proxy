package com.bin.kong.proxy.model.repeater.search;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistorySearch {
    private Integer max_id;
    private String kw;
}
