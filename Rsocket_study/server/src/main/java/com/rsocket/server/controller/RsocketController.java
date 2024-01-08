package com.rsocket.server.controller;

import com.rsocket.server.repository.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;

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

    // 2-1 Mono<Void>
    @MessageMapping("mono-faf")
    public Mono<Void> fafMono(Message req){
        log.info("Received faf req : {}", req);
        return Mono.empty();
    }

    // 3. Stream
    // 연속적인 메시지를 전송
    @MessageMapping("stream") // 클라이언트의 요청에 대해 스트림으로 연속적으로 메시지를 전송한다.
    // Flux를 사용해서 여러 값을 보낼 수 있다.
    Flux<Message> stream(Message req){
        return Flux.interval(Duration.ofSeconds(1)).map(i -> new Message("Test","server"));
    }
    @MessageMapping("FluxStream")
    Flux<Byte[]> fluxStream() {
        Path path = Path.of("/Users/ailak/Documents/Rsocket/Rsocket-spring-study/Rsocket_study/server/src/main/resources/file/opus.opus");
        File file = path.toFile();
        return Flux.create(sink -> {
            try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[320];
                int len;
                while ((len = bis.read(buffer)) > 0){ // read는 자동으로 내부 포인터가 읽기 위치를 업데이트
                    // 1번째 호출 read : 0 ~ 320
                    // 2번째 호출 read : 321 ~ 640 ...

                    // sink : stream 객체를 발행하는 객체
                    log.info("Sending {} bytes", len); // 로그 추가
                    sink.next(toByteObjectArray(Arrays.copyOf(buffer, len)));
                }
                sink.complete();
                log.info("File transmission complete"); // 전송 완료 로그
            }catch (IOException e){
                sink.error(e);
            }
        });
    }
    // 4. Channel
    @MessageMapping("channel") // 양방향 채널을 통해 메시지를 주고 받는다.
    Flux<Message> channel(final Flux<Duration> settings){
        // Flux<Duration>를 통해 설정 보내고, 서버는 해당 설정에 따른 메세지를 생성 응답한다
        log.info("settings : {}", settings);
        return settings.doOnNext(setting -> log.info("Frequency setting is {} second(s).", setting.getSeconds()))
                .switchMap(setting -> Flux.interval(setting).map(index -> new Message("Test", "server")));
    }


    private Byte[] toByteObjectArray(byte[] byteArray) {
        Byte[] byteObjects = new Byte[byteArray.length];
        int i = 0;
        for (byte b : byteArray) {
            byteObjects[i++] = b;  // Autoboxing
        }
        return byteObjects;
    }
}
