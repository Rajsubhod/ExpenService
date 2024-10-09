package com.rajsubhod.expenseservice.services;

import com.rajsubhod.expenseservice.dto.response.ExpenseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class SseEmitterService {

    public static class EmitterInfo {
        final SseEmitter emitter;
        Instant lastActive;

        public EmitterInfo(SseEmitter emitter, Instant lastActive) {
            this.emitter = emitter;
            this.lastActive = lastActive;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SseEmitterService.class);

    private static final long CONNECTION_TIMEOUT_MS = 3600000;
    private final Map<String, EmitterInfo> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(10);

    public SseEmitterService(){
        scheduler.scheduleAtFixedRate(this::removeExpiredConnections, 1, 1, TimeUnit.MINUTES);
    }

    public SseEmitter createEmitter(String userId) {
        // Check if an emitter with the given userId exists and remove it if it does
        if (emitters.containsKey(userId)) {
            removeEmitter(userId);
        }

        // Create a new emitter
        SseEmitter emitter = new SseEmitter(CONNECTION_TIMEOUT_MS);
        EmitterInfo emitterInfo = new EmitterInfo(emitter, Instant.now());
        emitters.put(userId, emitterInfo);

        emitter.onCompletion(() -> {
            logger.info("Emitter for user {} completed", userId);
            removeEmitter(userId);
        });
        emitter.onTimeout(() -> {
            logger.info("Emitter for user {} timed out", userId);
            removeEmitter(userId);
        });

        logger.info("Created new emitter for user {}", userId);
        return emitter;
    }

    public void sendToUser(String userId, ExpenseResponse expenseResponse) {

        Message<ExpenseResponse> message = MessageBuilder.withPayload(expenseResponse).build();
        EmitterInfo emitterInfo = emitters.get(userId);
        if (emitterInfo != null) {
            try {
                emitterInfo.emitter.send(SseEmitter.event().data(message));
                emitterInfo.lastActive = Instant.now(); // Update last active time
            } catch (IOException e) {
                removeEmitter(userId);
            }
        }
    }

    private void removeEmitter(String userId) {
        EmitterInfo removed = emitters.remove(userId);
        if (removed != null) {
            removed.emitter.complete();
        }
    }

    private void removeExpiredConnections() {
        Instant now = Instant.now();
        emitters.forEach((userId, emitterInfo) -> {
            if (now.isAfter(emitterInfo.lastActive.plusMillis(CONNECTION_TIMEOUT_MS))) {
                removeEmitter(userId);
            }
        });
    }

    public void shutdown() {
        scheduler.shutdown();
        emitters.forEach((userId, emitterInfo) -> emitterInfo.emitter.complete());
        emitters.clear();
    }

}
