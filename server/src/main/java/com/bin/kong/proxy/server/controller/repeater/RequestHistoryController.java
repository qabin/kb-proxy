package com.bin.kong.proxy.server.controller.repeater;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.repeater.RepeaterRequestHistoryMapper;
import com.bin.kong.proxy.model.repeater.entity.RepeaterRequestHistory;
import com.bin.kong.proxy.model.repeater.search.HistorySearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class RequestHistoryController {
    @Resource
    private RepeaterRequestHistoryMapper repeaterRequestHistoryMapper;

    @RequestMapping(value = "/history/_search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse history_list_search(@RequestParam(required = false) Integer maxId, @RequestParam(required = false) String kw) {
        GenericResponse response = new GenericResponse();
        try {
            List<RepeaterRequestHistory> requestHistoryList = repeaterRequestHistoryMapper.searchList(HistorySearch.builder()
                    .kw(kw)
                    .max_id(maxId)
                    .build());
            response.setData(requestHistoryList);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("history_list_search执行异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/history/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse delete_history_by_id(@PathVariable("id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            Integer count = repeaterRequestHistoryMapper.deleteByPrimaryKey(id);
            response.setData(count);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("delete_history_by_id执行异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/history/_delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse delete_all_history() {
        GenericResponse response = new GenericResponse();
        try {
            Integer count = repeaterRequestHistoryMapper.deleteByUserId(1);
            response.setData(count);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("delete_all_history执行异常：" + e);
        }
        return response;
    }
}
