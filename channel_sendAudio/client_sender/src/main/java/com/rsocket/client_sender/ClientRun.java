package com.rsocket.client_sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsocket.client_sender.protocol.Header;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClientRun {
    private final RSocketRequester rSocketRequester;

//    @Autowired
//    public ClientRun(RSocketRequester rSocketRequester){
//        this.rSocketRequester = rSocketRequester;
//    }

    public void run() throws JsonProcessingException {
        /*   client logic  */
        sendAudioChennel();

    }

    private void sendAudioChennel() throws JsonProcessingException {
        Header test = new Header("test", "test","test","OPUS_16K");
//        Path path = Path.of("D:\\01. project\\Rsocket-spring-study\\channel_sendAudio\\client_sender\\src\\main\\resources\\static\\audio\\test.wav");
        Path path = Path.of("/Users/ailak/Desktop/Rsocket/Rsocket-spring-study/channel_sendAudio/client_sender/src/main/resources/static/audio/test.wav");
        File file = path.toFile();
//        byte[] buffer = new byte[320];
//        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))

        String headerJson = new ObjectMapper().writeValueAsString(test);
//        ByteBuf metadata = ByteBufAllocator.DEFAULT.buffer().writeBytes(headerJson.getBytes(StandardCharsets.UTF_8));
        ByteBuf metadata = ByteBufAllocator.DEFAULT.buffer(headerJson.getBytes(StandardCharsets.UTF_8).length);
        metadata.writeBytes(headerJson.getBytes(StandardCharsets.UTF_8));

        System.out.println(headerJson);
        System.out.println(metadata);
        log.info("Header JSON: {}", headerJson);
        log.info("Metadata capacity: {}", metadata.capacity());


        rSocketRequester.route("audioSend")
//                .metadata(metadata, MimeTypeUtils.parseMimeType("message/x.rsocket.routing.v0"))
                .data(
                        Flux.create(fluxSink -> {
                            try(FileInputStream fis = new FileInputStream(file)){
                                byte[] buffer = new byte[320];
                                int bytesRead;
                                while((bytesRead = fis.read(buffer)) != -1){
                                    log.info("Send {} byte", bytesRead);
                                    fluxSink.next(Arrays.copyOf(buffer, bytesRead));
                                }
                                fluxSink.complete();
                            }catch (IOException e){
                                fluxSink.error(e);
                                log.info("error {}", e);
                            }
                        })
                ).retrieveFlux(String.class) // 여기를 수정: Flux<String>을 반환받음
                .subscribe(response -> {
                    log.info("Server response: {}", response);
                });


        // 클라이언트 코드의 데이터 전송 부분
//        rSocketRequester.route("audioSend")
////                .metadata(metadata, MimeTypeUtils.parseMimeType("message/x.rsocket.routing.v0"))
//                .data(Flux.just("테스트 데이터".getBytes())) // 테스트 데이터 전송
//                .retrieveFlux(String.class)
//                .subscribe(response -> {
//                    log.info("Server response: {}", response);
//                });




    }
}
