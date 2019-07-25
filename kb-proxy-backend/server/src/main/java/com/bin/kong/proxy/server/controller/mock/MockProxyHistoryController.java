package com.bin.kong.proxy.server.controller.mock;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.mock.MockProxyHistoryMapper;
import com.bin.kong.proxy.model.mock.entity.MockProxyHistory;
import com.bin.kong.proxy.model.mock.search.MockProxyHistorySearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MockProxyHistoryController {
    @Resource
    private MockProxyHistoryMapper proxyHistoryMapper;

    @RequestMapping(value = "/mock/proxy/history/_search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse _search(@RequestParam(required = false) Integer mock_id, @RequestParam(required = false) String kw, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        GenericResponse response = new GenericResponse();

        try {
            MockProxyHistorySearch search = MockProxyHistorySearch.builder()
                    .mock_id(mock_id)
                    .kw(kw)
                    .page(page)
                    .size(size)
                    .build();
            List<MockProxyHistory> proxyHistoryList = proxyHistoryMapper.searchList(search);
            Integer count = proxyHistoryMapper.searchCount(search);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("count", count);
            resultMap.put("data", proxyHistoryList);
            response.setData(resultMap);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行_search异常：" + e);
        }
        return response;
    }
}
