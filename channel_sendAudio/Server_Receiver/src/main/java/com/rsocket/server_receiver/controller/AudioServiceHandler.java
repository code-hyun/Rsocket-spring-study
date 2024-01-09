package com.rsocket.server_receiver.controller;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Controller
@Slf4j
public class AudioServiceHandler {

    private boolean isHeaderProcessed = false;

    @MessageMapping("audioSend")
    public Flux<String> receiveAudioStream(Flux<byte[]> audioStream ) {
        Path outPutPath = Path.of("/Users/ailak/Desktop/Rsocket/Rsocket-spring-study/channel_sendAudio/Server_Receiver/src/main/resources/audio/test.wav");

        return audioStream.flatMap(data -> {
            if (!isHeaderProcessed) {
                // 헤더 데이터 처리
                String headerJson = new String(data, StandardCharsets.UTF_8);
                log.info("Received header: {}", headerJson);
                isHeaderProcessed = true;
                return Flux.just("Header processed");
            } else {
                try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPutPath.toFile()))){
                    byte[] byteChunk = data;
                    bos.write(byteChunk);
                    log.info("Received audio data: {} bytes", data.length);

                    if(data.length < 320){

                        return Flux.just("finalize");
                    }
                }catch (IOException e){
                    return Mono.error(e);
                }
                // 오디오 데이터 처리
                // 예: 파일로 저장, 변환 등
                return Flux.just("Received " + data.length + " bytes");
            }
        });
    }
}


