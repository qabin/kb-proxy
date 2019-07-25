package com.bin.kong.proxy.server.littleproxy;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpEntity {
    private HttpResponseStatus code;
    private StringBuffer body = new StringBuffer();
    private HttpHeaders httpHeaders;
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public synchronized void appendByte(byte[] array) {
        if (content == null) {
            content = array;
        } else {
            byte[] temp = new byte[content.length + array.length];
            System.arraycopy(content, 0, temp, 0, content.length);
            System.arraycopy(array, 0, temp, content.length, array.length);
            content = temp;
        }
    }
}
