package com.bin.kong.proxy.server.controller.env;

import com.bin.kong.proxy.contract.common.GenericResponse;
import com.bin.kong.proxy.core.constants.ResponseConstants;
import com.bin.kong.proxy.core.utils.HostUtils;
import com.bin.kong.proxy.dao.mapper.env.EnvInfoMapper;
import com.bin.kong.proxy.model.env.entity.EnvInfo;
import com.bin.kong.proxy.model.env.enums.EnvStatusEnum;
import com.bin.kong.proxy.model.env.search.EnvSearch;
import com.bin.kong.proxy.server.controller.BaseController;
import com.bin.kong.proxy.server.host.HostCache;
import com.bin.kong.proxy.server.littleproxy.LittleProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class EnvController extends BaseController {
    @Resource
    private EnvInfoMapper envInfoMapper;
    @Resource
    private HostCache hostCache;
    @Resource
    private LittleProxyServer proxyServer;

    @RequestMapping(value = "/env", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_add_env_info(@RequestBody EnvInfo envInfo) {
        GenericResponse response = new GenericResponse();
        try {
            envInfo.setCreate_time(new Date());
            envInfo.setUpdate_time(new Date());
            envInfo.setUser_id(super.getUserInfo().getId());
            envInfoMapper.insertSelective(envInfo);
            response.setData(envInfo);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行ajax_add_env_info异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/env/_search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_env_info_search(@RequestParam(required = false) String kw) {
        GenericResponse response = new GenericResponse();
        try {
            List<EnvInfo> envInfoList = envInfoMapper.searchList(EnvSearch.builder()
                    .kw(kw)
                    .user_id(super.getUserInfo().getId())
                    .build());
            response.setData(envInfoList);
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行ajax_env_info_search异常：" + e);
        }
        return response;
    }


    @RequestMapping(value = "/env/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_delete_env_info(@PathVariable("id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            envInfoMapper.deleteByPrimaryKey(id);

            hostCache.remove(super.getUserInfo().getId());
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行ajax_delete_env_info异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/env/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_update_env_info(@PathVariable("id") Integer id, @RequestBody EnvInfo envInfo) {
        GenericResponse response = new GenericResponse();
        try {
            envInfo.setUpdate_time(new Date());
            envInfo.setId(id);
            envInfoMapper.updateByPrimaryKeySelective(envInfo);
            response.setData(envInfo);
            hostCache.put(super.getUserInfo().getId(), HostUtils.getHostMapFromString(envInfo.getHosts()));
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行ajax_update_env_info异常：" + e);
        }
        return response;
    }

    @RequestMapping(value = "/env/{id}/_start", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_env_info_start(@PathVariable("id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {

            EnvInfo envInfo = envInfoMapper.selectByPrimaryKey(id);

            envInfoMapper.updateByUserIdSelective(EnvInfo.builder()
                    .status(EnvStatusEnum.STOP.getCode())
                    .user_id(super.getUserInfo().getId())
                    .build());

            envInfoMapper.updateByPrimaryKeySelective(EnvInfo.builder()
                    .id(id)
                    .status(EnvStatusEnum.START.getCode())
                    .build());

            hostCache.put(super.getUserInfo().getId(), HostUtils.getHostMapFromString(envInfo.getHosts()));
            proxyServer.restartProxy(super.getUserInfo().getId());
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行ajax_env_info_start异常：" + e);
        }
        return response;
    }


    @RequestMapping(value = "/env/{id}/_stop", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse ajax_env_info_stop(@PathVariable("id") Integer id) {
        GenericResponse response = new GenericResponse();
        try {

            envInfoMapper.updateByPrimaryKeySelective(EnvInfo.builder()
                    .id(id)
                    .status(EnvStatusEnum.STOP.getCode())
                    .build());

            hostCache.remove(super.getUserInfo().getId());
            proxyServer.restartProxy(super.getUserInfo().getId());
            response.setStatus(ResponseConstants.SUCCESS_CODE);
        } catch (Exception e) {
            response.setStatus(ResponseConstants.FAIL_CODE);
            log.error("执行ajax_env_info_stop异常：" + e);
        }
        return response;
    }
}
