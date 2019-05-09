package com.bin.kong.proxy.dao.mapper.repeater;

import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestDetail;

public interface RepeaterRequestDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(RepeaterRequestDetail record);

    RepeaterRequestDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RepeaterRequestDetail record);

    int deleteByFolderId(Integer id);
}
