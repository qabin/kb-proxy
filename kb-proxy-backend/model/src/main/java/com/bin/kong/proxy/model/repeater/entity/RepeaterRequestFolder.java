package com.bin.kong.proxy.model.repeater.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeaterRequestFolder {
    private Integer id;
    private String name;
    private Date create_time;
    private Integer user_id;
}
