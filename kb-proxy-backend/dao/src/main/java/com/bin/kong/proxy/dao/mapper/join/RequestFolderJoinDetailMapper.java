package com.bin.kong.proxy.dao.mapper.join;

import com.bin.kong.proxy.model.join.entity.RequestFolderJoinDetail;
import com.bin.kong.proxy.model.repeater.search.CollectionSearch;

import java.util.List;

public interface RequestFolderJoinDetailMapper {
    List<RequestFolderJoinDetail> searchListWithFolder(CollectionSearch search);
}
