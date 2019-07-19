package com.bin.kong.proxy.model.env.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnvInfo {
    private Integer id;

    private String name;

    private Integer user_id;

    private Date create_time;

    private Date update_time;

    private Integer status;

    private String hosts;
}
