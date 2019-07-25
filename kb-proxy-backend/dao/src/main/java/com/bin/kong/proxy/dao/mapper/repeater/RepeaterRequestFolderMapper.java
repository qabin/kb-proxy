package com.bin.kong.proxy.dao.mapper.repeater;

import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestFolder;
import com.bin.kong.proxy.model.repeater.search.FolderSearch;

import java.util.List;

public interface RepeaterRequestFolderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RepeaterRequestFolder record);

    int insertSelective(RepeaterRequestFolder record);

    RepeaterRequestFolder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RepeaterRequestFolder record);

    int updateByPrimaryKey(RepeaterRequestFolder record);

    List<RepeaterRequestFolder> searchList(FolderSearch search);

}
