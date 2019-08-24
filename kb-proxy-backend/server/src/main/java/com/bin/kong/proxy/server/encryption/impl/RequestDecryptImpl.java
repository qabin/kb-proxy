package com.bin.kong.proxy.server.encryption.impl;

import com.bin.kong.proxy.server.encryption.EncryptionUtils;
import com.bin.kong.proxy.server.encryption.IRequestDecrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//请求解密，用于解密请求信息，明文保存入数据库和显示使用，不影响实际发送数据流
//注：目前只处理request 的body内容解密
@Service
public class RequestDecryptImpl implements IRequestDecrypt {
    @Value("${encryption.domain_list:#{null}}")
    private String domain_list;

    @Override
    public String decrypt(String domain, String input) {

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
