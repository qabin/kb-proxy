package com.bin.kong.proxy.server.encryption.impl;

import com.bin.kong.proxy.server.encryption.EncryptionUtils;
import com.bin.kong.proxy.server.encryption.IRequestEncrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
//请求加密，用于接口重发测试使用，保证实际发送的数据流是加密的
public class RequestEncryptImpl implements IRequestEncrypt {
    @Value("${encryption.domain_list:#{null}}")
    private String domain_list;

    @Override
    public String encrypt(String domain,String input) {
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
