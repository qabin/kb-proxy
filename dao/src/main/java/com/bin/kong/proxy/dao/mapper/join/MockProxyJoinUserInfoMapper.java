package com.bin.kong.proxy.dao.mapper.join;

import com.bin.kong.proxy.model.join.entity.MockProxyJoinUserInfo;
import com.bin.kong.proxy.model.mock.search.MockProxySearch;

import java.util.List;

public interface MockProxyJoinUserInfoMapper {
    List<MockProxyJoinUserInfo> searchList(MockProxySearch search);

    int searchCount(MockProxySearch search);

}
