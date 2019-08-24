package com.bin.kong.proxy.server.encryption;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class EncryptionUtils {

    public static boolean contains_domain(String domain_list, String domain) {
        if (!StringUtils.isEmpty(domain_list) && !StringUtils.isEmpty(domain)) {
            List<String> domainList = Arrays.asList(domain_list.split(","));
            if (domain.indexOf(":") != -1) {
                domain = domain.substring(0, domain.indexOf(":"));
            }
            if (domainList.contains(domain)) {
                return true;
            }
        }
        return false;
    }
}
