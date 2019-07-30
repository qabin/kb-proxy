package com.bin.kong.proxy.model.repeater.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorySearch {
    private Integer max_id;
    private String kw;
    private Integer user_id;
}
