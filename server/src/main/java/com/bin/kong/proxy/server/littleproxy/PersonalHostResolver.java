package com.bin.kong.proxy.server.littleproxy;

import com.bin.kong.proxy.server.host.HostCache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.littleshoot.proxy.DefaultHostResolver;
import org.springframework.stereotype.Service;
import sun.net.util.IPAddressUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Service
public class PersonalHostResolver extends DefaultHostResolver {

    private Integer userPort;

    @Override
    public InetSocketAddress resolve(String host, int port) {
        InetAddress addr = null;
        try {
            HostCache hostCache = new HostCache();
            Map<String, String> hostMap = hostCache.get(this.userPort);
            if (null != hostMap) {
                if (hostMap.containsKey(host)) {
                    String ip = hostMap.get(host);
                    addr = InetAddress.getByAddress(host, getBytesFromString(ip));
                } else {
                    addr = InetAddress.getByName(host);
                }

            } else {
                addr = InetAddress.getByName(host);
            }
        } catch (Exception e) {

        }
        return new InetSocketAddress(addr, port);
    }

    private byte[] getBytesFromString(String ip) {
        byte[] result = new byte[0];
        if (IPAddressUtil.isIPv4LiteralAddress(ip)) {
            result = IPAddressUtil.textToNumericFormatV4(ip);
        } else if (IPAddressUtil.isIPv6LiteralAddress(ip)) {
            result = IPAddressUtil.textToNumericFormatV6(ip);
        }
        return result;
    }
}
