package com.bin.kong.proxy.server.littleproxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ProxyToServerResolutionStartedFilter {
    private final static Integer DEFAULT_PORT = 80;

    public InetSocketAddress dnsResolve(String hostAndPort, Integer userPort) {
        int port = DEFAULT_PORT;
        String host;
        if (hostAndPort.indexOf(":") != -1) {
            List<String> hostAndIpList = Arrays.asList(hostAndPort.split(":"));
            host = hostAndIpList.get(0);
            port = Integer.valueOf(hostAndIpList.get(1));
        } else {
            host = hostAndPort;
        }

        return PersonalHostResolver.builder().userPort(userPort).build().resolve(host, port);
    }
}
