package com.example.demo3springaimessenger.global.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SseEmitterPool {

    private static class Entry {
        final SseEmitter emitter;
        volatile Instant touched;
        Entry(SseEmitter e) { this.emitter = e; this.touched = Instant.now(); }
    }

    private final ConcurrentMap<String, Entry> map = new ConcurrentHashMap<>();
    private final long ttlSeconds = 3600; // 1시간 idle TTL

    public void put(String clientId, SseEmitter emitter) {
        Entry old = map.put(clientId, new Entry(emitter));
        if (old != null) {
            try { old.emitter.complete(); } catch (Exception ignore) {}
        }
    }

    public SseEmitter get(String clientId) {
        Entry e = map.get(clientId);
        if (e != null) e.touched = Instant.now();
        return e == null ? null : e.emitter;
    }

    public void remove(String clientId, SseEmitter current) {
        map.computeIfPresent(clientId, (k, e) -> (e.emitter == current) ? null : e);
    }

    /** 스케줄러에서 주기적으로 호출해 청소 */
    public void cleanup() {
        Instant now = Instant.now();
        for (Map.Entry<String, Entry> en : map.entrySet()) {
            if (now.getEpochSecond() - en.getValue().touched.getEpochSecond() > ttlSeconds) {
                SseEmitter em = en.getValue().emitter;
                map.remove(en.getKey(), en.getValue());
                try { em.complete(); } catch (Exception ignore) {}
            }
        }
    }
}
