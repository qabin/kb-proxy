package com.bin.kong.proxy.server.encryption;

public interface IRequestDecrypt {
    String decrypt(String domain,String input);
}
