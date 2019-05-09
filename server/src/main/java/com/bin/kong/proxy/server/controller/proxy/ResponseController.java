package com.bin.kong.proxy.server.controller.proxy;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.proxy.ResponseDetailMapper;
import com.bin.kong.proxy.model.proxy.entity.ResponseDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class ResponseController {
    @Resource
    private ResponseDetailMapper responseDetailMapper;

    @RequestMapping(value = "/responses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse get_response_detail_by_request_id(@RequestParam Integer request_detail_id) {
        GenericResponse response = new GenericResponse();
        try {
            List<ResponseDetail> responseDetailList = responseDetailMapper.selectList(ResponseDetail.builder()
                    .request_detail_id(request_detail_id)
                    .build());
            response.setStatus(ResponseConstants.SUCCESS_CODE);
            if (responseDetailList != null && responseDetailList.size() > 0)
                response.setData(responseDetailList.get(0));
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行get_response_detail_by_id异常：" + e);
        }
        return response;
    }
}
