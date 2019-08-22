package com.bin.kong.proxy.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class OSUtils {
    /**
     * 判断系统是否是windows
     *
     * @return
     */
    public static boolean isWindows() {
        String os = System.getProperties().getProperty("os.name");
        if (!StringUtils.isEmpty(os)) {
            return os.toUpperCase().indexOf("WINDOWS") != -1;
        }

        return false;
    }
}
