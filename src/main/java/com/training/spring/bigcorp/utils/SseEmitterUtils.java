package com.training.spring.bigcorp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class SseEmitterUtils {
    private static final Logger logger =
            LoggerFactory.getLogger(SseEmitterUtils.class);
    private Set<SseEmitter> emitters = new HashSet<>();
    public SseEmitter createEmitter(){
        SseEmitter sseEmitter = new SseEmitter(2000L);
        emitters.add(sseEmitter);
        sseEmitter.onCompletion(() -> this.remove(sseEmitter, null));
        sseEmitter.onTimeout(() -> this.remove(sseEmitter, null));
        sseEmitter.onError((err) -> this.remove(sseEmitter, err));
        return sseEmitter;
    }
    private void remove(SseEmitter emitter, Throwable error){
        if(error != null){
            logger.error("Error on sseEmitter", error);
        }
        if(emitters.contains(emitter)){
            emitters.remove(emitter);
        }
    }
    public Set<SseEmitter> getEmitters() {
        return Collections.unmodifiableSet(emitters);
    }
}