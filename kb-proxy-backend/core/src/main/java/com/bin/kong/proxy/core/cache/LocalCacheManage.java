package com.bin.kong.proxy.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Component
@Slf4j
public class LocalCacheManage implements CommandLineRunner {
    @Autowired
    private List<ILocalCache> localCaches;

    @Override
    public void run(String... args) throws Exception {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (localCaches != null && !localCaches.isEmpty()) {
                    for (ILocalCache cache : localCaches) {
                        try {
                            cache.refresh();
                        } catch (Exception e) {
                            log.error("本地缓存更新异常:" + e);
                        }
                    }
                }
            }
        }, 0, 60000);
    }
}
