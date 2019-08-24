package com.bin.kong.proxy.server.encryption.impl;

import com.bin.kong.proxy.server.encryption.EncryptionUtils;
import com.bin.kong.proxy.server.encryption.IResponseEncrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
//响应加密，用于Mock数据的加密，保证实际发送的数据流是加密过的
public class ResponseEncryptImpl implements IResponseEncrypt {
    @Value("${encryption.domain_list:#{null}}")
    private String domain_list;

    @Override
    public String encrypt(String domain, String input) {
        if (!EncryptionUtils.contains_domain(domain_list, domain)) {
            return input;
        }

        try {
            //此处实现相应的逻辑

            return input;
        } catch (Exception e) {
            return input;
        }
    }
}
