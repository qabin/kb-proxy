package com.bin.kong.proxy.dao.mapper.repeater;

import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestHistory;
import com.bin.kong.proxy.model.repeater.search.HistorySearch;

import java.util.List;

public interface RepeaterRequestHistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(RepeaterRequestHistory record);

    RepeaterRequestHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RepeaterRequestHistory record);

    List<RepeaterRequestHistory> searchList(HistorySearch search);

    int deleteByUserId(Integer id);
}
