package com.bin.kong.proxy.server.encryption.impl;

import com.bin.kong.proxy.server.encryption.EncryptionUtils;
import com.bin.kong.proxy.server.encryption.IResponseDecrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
//响应解密，用于解密响应结果，明文保存入数据库和显示使用，不影响实际发送数据流
public class ResponseDecryptImpl implements IResponseDecrypt {
    @Value("${encryption.domain_list:#{null}}")
    private String domain_list;

    @Override
    public String decrypt(String domain,String input) {
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
