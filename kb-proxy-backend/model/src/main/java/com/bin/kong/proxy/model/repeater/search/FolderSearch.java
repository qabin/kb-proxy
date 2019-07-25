package com.bin.kong.proxy.model.repeater.search;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderSearch {
    private String kw;
    private Integer user_id;
}
