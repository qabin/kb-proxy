package com.bin.kong.proxy.dao.mapper.proxy;

import com.bin.kong.proxy.model.proxy.entity.ResponseDetail;

import java.util.List;

public interface ResponseDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByPort(Integer proxy_port);

    int insertSelective(ResponseDetail record);

    ResponseDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResponseDetail record);

    List<ResponseDetail> selectList(ResponseDetail record);
}
