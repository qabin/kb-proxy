package com.bin.kong.proxy.server.encryption;

public interface IRequestEncrypt {
    String encrypt(String domain,String input);
}
