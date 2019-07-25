package com.bin.kong.proxy.server.controller.repeater;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.repeater.RepeaterRequestDetailMapper;
import com.bin.kong.proxy.dao.mapper.repeater.RepeaterRequestFolderMapper;
import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestFolder;
import com.bin.kong.proxy.model.repeater.search.FolderSearch;
import com.bin.kong.proxy.server.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class RequestFolderController extends BaseController {
    @Resource
    private RepeaterRequestFolderMapper requestFolderMapper;
    @Resource
    private RepeaterRequestDetailMapper repeaterRequestDetailMapper;

    @RequestMapping(value = "/folders", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse add_folder(@RequestBody RepeaterRequestFolder folder) {
        GenericResponse response = new GenericResponse();
        try {
            Integer count = requestFolderMapper.insertSelective(RepeaterRequestFolder.builder()
                    .create_time(new Date())
                    .user_id(super.getUserInfo().getId())
                    .name(folder.getName())
                    .build());
            response.setStatus(ResponseConstants.SUCCESS_CODE);
            response.setData(count);
        } catch (Exception e) {
            log.error("add_folder执行异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }
        return response;
    }

    @RequestMapping(value = "/folders/_search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse search_folder(@RequestParam(required = false) String kw) {
        GenericResponse response = new GenericResponse();
        try {
            List<RepeaterRequestFolder> folderList = requestFolderMapper.searchList(FolderSearch.builder()
                    .kw(kw)
                    .user_id(super.getUserInfo().getId())
                    .build());
            response.setStatus(ResponseConstants.SUCCESS_CODE);
            response.setData(folderList);
        } catch (Exception e) {
            log.error("add_folder执行异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }
        return response;
    }

    @RequestMapping(value = "/folders/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse folder_update(@PathVariable("id") Integer id, @RequestBody RepeaterRequestFolder folder) {
        GenericResponse response = new GenericResponse();
        try {

            requestFolderMapper.updateByPrimaryKeySelective(RepeaterRequestFolder.builder()
                    .name(folder.getName())
                    .id(id)
                    .build());
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            log.error("folder_update执行异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }
        return response;
    }


    @RequestMapping(value = "/folders/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse folder_delete(@PathVariable("id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            Integer count = requestFolderMapper.deleteByPrimaryKey(id);
            response.setData(count);
            //删除文件夹，自动删除文件夹下所有的请求
            repeaterRequestDetailMapper.deleteByFolderId(id);

            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            log.error("folder_delete执行异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }
        return response;
    }
}
