package com.rsocket.server.controller;

import com.rsocket.server.repository.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * MessageMapping : Spring에서 Rsocket 통신을 위해 사용되는 어노테이션
 * Rsocket은 Reactive Streams와 함께 사용되고, 양방향 통신을 위한 프로토콜
 *
 * */
@Controller
@Slf4j
public class RsocketController {

    // 1. request - response
    @MessageMapping("req-res") // 요청에 대한 1번의 응답 (http 통신과 비슷하다)
    public Message reqRes(Message req) {
        log.info("Received request-response request: {}", req);
        return new Message("Test", "server");
    }
    // 1-1 Mono
    @MessageMapping("mono-req-res")
    public Mono<String> reqResMono(Mono<String> request){
        return request.map(req -> {
           log.info("Mono req-res | id value : {}", req);
           return "ID OK";
        });
    }

    // 2. Fire-And-Forget
    @MessageMapping("fire-and-forget") // 클라이언트는 메시지를 보내고 서버는 응답하지 않는다
    public void faf(Message req){
        log.info("Received request-response request: {}", req);
    }
    // 3. Stream
    @MessageMapping("stream") // 클라이언트의 요청에 대해 스트림으로 연속적으로 메시지를 전송한다.
    Flux<Message> stream(Message req){
        return Flux.interval(Duration.ofSeconds(1)).map(i -> new Message("Test","server"));
    }
    // 4. Channel
    @MessageMapping("channel") // 양방향 채널을 통해 메시지를 주고 받는다.
    Flux<Message> channel(final Flux<Duration> settings){
        // Flux<Duration>를 통해 설정 보내고, 서버는 해당 설정에 따른 메세지를 생성 응답한다
        log.info("settings : {}", settings);
        return settings.doOnNext(setting -> log.info("Frequency setting is {} second(s).", setting.getSeconds()))
                .switchMap(setting -> Flux.interval(setting).map(index -> new Message("Test", "server")));
    }
}
