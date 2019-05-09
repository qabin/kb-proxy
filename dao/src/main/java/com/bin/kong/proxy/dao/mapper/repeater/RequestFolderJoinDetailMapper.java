package com.bin.kong.proxy.dao.mapper.repeater;

import com.bin.kong.proxy.model.repeater.entity.RequestDetailJoinFolder;
import com.bin.kong.proxy.model.repeater.search.CollectionSearch;

import java.util.List;

public interface RequestFolderJoinDetailMapper {
    List<RequestDetailJoinFolder> searchListWithFolder(CollectionSearch search);
}
