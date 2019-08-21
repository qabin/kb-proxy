package com.bin.kong.proxy.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HostUtils {
    /**
     * string host to map
     *
     * @param input
     * @return
     */
    public static Map<String, String> getHostMapFromString(String input) {
        Map<String, String> resultMap = new HashMap<>();
        if (!StringUtils.isEmpty(input)) {
            input = input.replaceAll("\r\n", "\n");
            input = input.replaceAll("\r", "\n");
            List<String> hostAndIpStrList = Arrays.asList(input.split("\n"));
            if (!CollectionUtils.isEmpty(hostAndIpStrList)) {
                for (String s : hostAndIpStrList) {
                    String hostAndIpStr = s.trim();
                    if (hostAndIpStr.indexOf(" ") != -1) {
                        try {
                            String ip = hostAndIpStr.substring(0, hostAndIpStr.indexOf(" ")).trim();
                            String domain = hostAndIpStr.substring(hostAndIpStr.indexOf(" ")).trim();
                            if (!ip.startsWith("#"))
                                resultMap.put(domain, ip);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return resultMap;
    }
}
