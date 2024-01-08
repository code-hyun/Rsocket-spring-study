package com.rsocket.server.controller;

import com.rsocket.server.repository.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
@Slf4j
public class RsocketController {

    // 1. request - response
    @MessageMapping("req-res")
    public Message reqRes(Message req) {
        log.info("Received request-response request: {}", req);
        return new Message("superpil", "server");
    }
    // 2. Fire-And-Forget
    @MessageMapping("fire-and-forget")
    public void faf(Message req){
        log.info("Received request-response request: {}", req);
    }
    // 3. Stream
    @MessageMapping("stream")
    Flux<Message> stream(Message req){
        return Flux.interval(Duration.ofSeconds(1)).map(i -> new Message("superpil","server"));
    }
    // 4. Channel
    @MessageMapping("channel")
    Flux<Message> channel(final Flux<Duration> settings){
        log.info("settings : {}", settings);
        return settings.doOnNext(setting -> log.info("Frequency setting is {} second(s).", setting.getSeconds()))
                .switchMap(setting -> Flux.interval(setting).map(index -> new Message("superpil", "server")));
    }
}
