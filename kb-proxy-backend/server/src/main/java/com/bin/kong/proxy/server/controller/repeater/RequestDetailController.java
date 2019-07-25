package com.bin.kong.proxy.server.controller.repeater;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.repeater.RepeaterRequestDetailMapper;
import com.bin.kong.proxy.dao.mapper.join.RequestFolderJoinDetailMapper;
import com.bin.kong.proxy.model.join.entity.RequestFolderJoinDetail;
import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestDetail;
import com.bin.kong.proxy.model.repeater.search.CollectionSearch;
import com.bin.kong.proxy.server.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class RequestDetailController extends BaseController {
    @Resource
    private RequestFolderJoinDetailMapper folderJoinDetailMapper;
    @Resource
    private RepeaterRequestDetailMapper repeaterRequestDetailMapper;

    @RequestMapping(value = "/details/_search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse detail_list(@RequestParam(required = false) String kw) {
        GenericResponse response = new GenericResponse();
        try {
            List<RequestFolderJoinDetail> detailJoinFolderList = folderJoinDetailMapper.searchListWithFolder(CollectionSearch.builder()
                    .kw(kw)
                    .user_id(super.getUserInfo().getId())
                    .build());
            response.setData(detailJoinFolderList);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("detail_list执行异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/details/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse detail_delete(@PathVariable("id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            Integer count = repeaterRequestDetailMapper.deleteByPrimaryKey(id);
            response.setData(count);

            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("detail_delete执行异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/details/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse detail_update(@PathVariable("id") Integer id, @RequestBody RepeaterRequestDetail detail) {
        GenericResponse response = new GenericResponse();
        try {
            RepeaterRequestDetail requestDetail = detail;
            requestDetail.setUpdate_time(new Date());
            requestDetail.setId(id);
            Integer count = repeaterRequestDetailMapper.updateByPrimaryKeySelective(requestDetail);
            response.setData(count);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("detail_update执行异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse add_detail(@RequestBody RepeaterRequestDetail detail) {
        GenericResponse response = new GenericResponse();
        try {
            RepeaterRequestDetail requestDetail = detail;
            requestDetail.setUser_id(super.getUserInfo().getId());
            requestDetail.setCreate_time(new Date());
            requestDetail.setUpdate_time(new Date());
            Integer count = repeaterRequestDetailMapper.insertSelective(requestDetail);
            response.setData(count);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("add_detail执行异常：" + e);
        }
        return response;
    }
}
