package com.rsocket.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import com.rsocket.server.repository.Message;
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
        return Flux.interval(Duration.ofSeconds(1)).map(i -> new Message("superil","server"));
    }
    // 4. Channel
    @MessageMapping
    Flux<Message> channel(final Flux<Duration>)
}
