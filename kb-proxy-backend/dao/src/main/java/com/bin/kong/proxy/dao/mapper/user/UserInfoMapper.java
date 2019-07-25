package com.bin.kong.proxy.dao.mapper.user;

import com.bin.kong.proxy.model.user.entity.UserInfo;

import java.util.List;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    UserInfo selectByLoginName(String loginName);

    List<UserInfo> selectList(UserInfo record);

}
