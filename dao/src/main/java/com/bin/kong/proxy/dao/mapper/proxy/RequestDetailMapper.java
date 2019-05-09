package com.bin.kong.proxy.dao.mapper.proxy;

import com.bin.kong.proxy.model.proxy.entity.RequestDetail;
import com.bin.kong.proxy.model.proxy.search.RequestSearch;

import java.util.List;

public interface RequestDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByPort(Integer proxy_port);

    int insertSelective(RequestDetail record);

    RequestDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RequestDetail record);

    List<RequestDetail> searchList(RequestSearch search);
}
