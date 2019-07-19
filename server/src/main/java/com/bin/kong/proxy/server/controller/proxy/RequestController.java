package com.bin.kong.proxy.server.controller.proxy;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.proxy.RequestDetailMapper;
import com.bin.kong.proxy.model.proxy.entity.RequestDetail;
import com.bin.kong.proxy.model.proxy.search.RequestSearch;
import com.bin.kong.proxy.server.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class RequestController extends BaseController {
    @Resource
    private RequestDetailMapper requestDetailMapper;

    /**
     * 请求记录 搜索
     *
     * @param maxId
     * @return
     */
    @RequestMapping(value = "/requests/_search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse request_list_search(@RequestParam Integer maxId, @RequestParam(required = false) String kw) {
        GenericResponse response = new GenericResponse();
        try {
            List<RequestDetail> requestDetailList = requestDetailMapper.searchList(RequestSearch.builder()
                    .max_id(maxId)
                    .kw(kw)
                    .build());
            response.setData(requestDetailList);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行request_list_search异常：" + e);
        }
        return response;
    }

    /**
     * 操作请求列表
     *
     * @return
     */
    @RequestMapping(value = "/requests", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse request_list_delete() {
        GenericResponse response = new GenericResponse();
        try {
            Integer count = requestDetailMapper.deleteByPort(super.getUserInfo().getId());
            response.setData(count);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行request_list_delete异常：" + e);
        }
        return response;
    }

    /**
     * 根据ID查看请求记录
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/requests/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse get_request_by_id(@PathVariable("id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            RequestDetail requestDetail = requestDetailMapper.selectByPrimaryKey(id);
            response.setData(requestDetail);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行get_request_by_id异常:" + e);
        }
        return response;
    }

}
