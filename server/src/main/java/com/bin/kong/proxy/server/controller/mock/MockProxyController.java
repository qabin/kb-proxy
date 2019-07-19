package com.bin.kong.proxy.server.controller.mock;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.dao.mapper.mock.MockProxyMapper;
import com.bin.kong.proxy.model.mock.entity.MockProxy;
import com.bin.kong.proxy.model.mock.search.MockProxySearch;
import com.bin.kong.proxy.server.controller.BaseController;
import com.bin.kong.proxy.server.mock.MockProxyCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MockProxyController extends BaseController {
    @Resource
    private MockProxyMapper mockProxyMapper;
    @Resource
    private MockProxyCache mockProxyCache;

    @RequestMapping(value = "/mock/proxy/_search")
    public GenericResponse _search(@RequestParam(required = false) String kw, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        GenericResponse response = new GenericResponse();
        try {
            MockProxySearch search = MockProxySearch.builder()
                    .kw(kw)
                    .user_id(super.getUserInfo().getId())
                    .page(page)
                    .size(size)
                    .build();
            List<MockProxy> MockProxyList = mockProxyMapper.searchList(search);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
            Integer count = mockProxyMapper.searchCount(search);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", MockProxyList);
            resultMap.put("count", count);
            response.setData(resultMap);
        } catch (Exception e) {
            log.error("执行_search异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }

        return response;
    }

    @RequestMapping(value = "/mock/proxy/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_update_mock(@PathVariable(value = "id") Integer id, @RequestBody MockProxy body) {
        GenericResponse response = new GenericResponse();
        try {
            MockProxy oldMockProxy = mockProxyMapper.selectByPrimaryKey(id);
            MockProxy mockProxy = MockProxy.builder()
                    .id(id)
                    .update_time(new Date())
                    .code(body.getCode())
                    .url(body.getUrl())
                    .response(body.getResponse())
                    .method(body.getMethod())
                    .headers(body.getHeaders())
                    .name(body.getName())
                    .description(body.getDescription())
                    .is_used(body.getIs_used())
                    .only_uri(body.getOnly_uri())
                    .build();
            Integer count = mockProxyMapper.updateByPrimaryKeySelective(mockProxy);
            response.setData(count);
            //更新缓存
            if (mockProxy.getIs_used() == 1) {
                mockProxyCache.remove(mockProxyCache.getCacheKey(oldMockProxy.getUrl(), oldMockProxy.getOnly_uri(), super.getUserInfo().getId()));
                mockProxyCache.put(mockProxyCache.getCacheKey(mockProxy.getUrl(), mockProxy.getOnly_uri(), super.getUserInfo().getId()), mockProxy);
            } else {
                mockProxyCache.remove(mockProxyCache.getCacheKey(oldMockProxy.getUrl(), oldMockProxy.getOnly_uri(), super.getUserInfo().getId()));
            }

            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            log.error("执行ajax_update_mock异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }

        return response;
    }

    @RequestMapping(value = "/mock/proxy/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_get_mock(@PathVariable(value = "id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            MockProxy mockProxy = mockProxyMapper.selectByPrimaryKey(id);
            response.setData(mockProxy);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            log.error("执行ajax_get_mock异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }

        return response;
    }


    @RequestMapping(value = "/mock/proxy/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_delete_mock(@PathVariable(value = "id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            MockProxy mockProxy = mockProxyMapper.selectByPrimaryKey(id);
            //删除缓存
            mockProxyCache.remove(mockProxy.getUrl(), super.getUserInfo().getId());
            Integer count = mockProxyMapper.deleteByPrimaryKey(id);
            response.setData(count);

            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            log.error("执行ajax_delete_mock异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }

        return response;
    }


    @RequestMapping(value = "/mock/proxy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse add_mock_proxy(@RequestBody MockProxy body) {
        GenericResponse response = new GenericResponse();
        try {
            MockProxy mockProxy = MockProxy.builder()
                    .code(body.getCode())
                    .create_time(new Date())
                    .update_time(new Date())
                    .is_used(body.getIs_used())
                    .description(body.getDescription())
                    .name(body.getName())
                    .headers(body.getHeaders())
                    .method(body.getMethod())
                    .response(body.getResponse())
                    .url(body.getUrl())
                    .domain(getDomainFormUrl(body.getUrl()))
                    .user_id(super.getUserInfo().getId())
                    .only_uri(body.getOnly_uri())
                    .build();
            mockProxyMapper.insertSelective(mockProxy);
            //添加缓存
            mockProxyCache.put(mockProxyCache.getCacheKey(mockProxy.getUrl(), mockProxy.getOnly_uri(), super.getUserInfo().getId()), mockProxy);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
            response.setData(mockProxy.getId());
        } catch (Exception e) {
            log.error("执行add_mock_proxy异常：" + e);
            response.setStatus(ResponseConstants.FAIL_CODE);
        }

        return response;
    }

    /**
     * 根据url获取domain
     *
     * @param url
     * @return
     */
    private String getDomainFormUrl(String url) {
        if (url.startsWith("http://")) {
            url = url.replaceAll("http://", "");
            if (url.indexOf("/") != -1) {
                url = url.substring(0, url.indexOf("/"));
            }
            return url;
        } else if (url.startsWith("https://")) {
            url = url.replaceAll("https://", "");
            if (url.indexOf("/") != -1) {
                url = url.substring(0, url.indexOf("/"));
            }
            return url;
        } else {
            if (url.indexOf("/") != -1) {
                url = url.substring(0, url.indexOf("/"));
            }
        }
        return url;
    }

}
