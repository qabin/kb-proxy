package com.bin.kong.proxy.dao.mapper.env;

import com.bin.kong.proxy.model.env.entity.EnvInfo;
import com.bin.kong.proxy.model.env.search.EnvSearch;

import java.util.List;

public interface EnvInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(EnvInfo record);

    EnvInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EnvInfo record);

    List<EnvInfo> searchList(EnvSearch search);

    List<EnvInfo> selectList(EnvInfo search);

    int updateByUserIdSelective(EnvInfo record);

}
