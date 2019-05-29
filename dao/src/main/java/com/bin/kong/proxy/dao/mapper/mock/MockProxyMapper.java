package com.bin.kong.proxy.dao.mapper.mock;

import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.model.mock.search.MockProxySearch;

import java.util.List;

public interface MockProxyMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(MockProxy record);

    MockProxy selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MockProxy record);

    List<MockProxy> searchList(MockProxySearch search);

    int searchCount(MockProxySearch search);

}
