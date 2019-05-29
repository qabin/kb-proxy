package com.bin.kong.proxy.dao.mapper.mock;

import com.bin.kong.proxy.model.mock.entity.MockProxyHistory;
import com.bin.kong.proxy.model.mock.search.MockProxyHistorySearch;

import java.util.List;

public interface MockProxyHistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(MockProxyHistory record);

    MockProxyHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MockProxyHistory record);

    List<MockProxyHistory> searchList(MockProxyHistorySearch search);

    int searchCount(MockProxyHistorySearch search);
}
