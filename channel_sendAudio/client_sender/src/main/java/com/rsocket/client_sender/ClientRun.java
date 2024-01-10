package com.rsocket.client_sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsocket.client_sender.protocol.Header;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.DefaultMetadataExtractor;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

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
        sendAudioChannel();

    }

    private void sendAudioChannel() throws JsonProcessingException {
        Header test = new Header("test", "test","test","OPUS_16K"); //
        Path path = Path.of("D:\\01. project\\Rsocket-spring-study\\channel_sendAudio\\client_sender\\src\\main\\resources\\static\\audio\\test.wav");
//        Path path = Path.of("/Users/ailak/Desktop/Rsocket/Rsocket-spring-study/channel_sendAudio/client_sender/src/main/resources/static/audio/test.wav");
        File file = path.toFile();

//        MetadataExtractor extractor = new DefaultMetadataExtractor();


        String headerJson = new ObjectMapper().writeValueAsString(test); // header json 으로 만듬
//        ByteBuf metadata = ByteBufAllocator.DEFAULT.buffer().writeBytes(headerJson.getBytes(StandardCharsets.UTF_8));
//        ByteBuf metadata = ByteBufAllocator.DEFAULT.buffer(headerJson.getBytes(StandardCharsets.UTF_8).length);

        byte[] headerBytes = headerJson.getBytes(StandardCharsets.UTF_8);
        ByteBuf metadata = ByteBufAllocator.DEFAULT.buffer(headerBytes.length);
        metadata.writeBytes(headerBytes);

        /*  ByteBuf : netty의 data container (byteBuffer 편리하게 사용)
         *  ByteBufAllocator 인터페이스 : 메모리 할당과 해제시 발생하는 오버헤드를 줄이기 위해 ByteBufAllocator 인터페이스를 통해 ByteBuf 인스턴스를 할당하는데 이용 할 수 있는 풀링을 구현
         * */
        log.info("Header JSON: {}", headerJson);
        log.info("Metadata capacity: {}", metadata.capacity());

        rSocketRequester.route("audioSend") // route 지정 - audioSend
//                .metadata(metadata, MimeTypeUtils.parseMimeType("message/x.rsocket.routing.v0"))
                .data( // 보낼 데이터
                        Flux.create(fluxSink -> { // Flux 생성
                            try(FileInputStream fis = new FileInputStream(file)){ // audio file을 inputStream 읽음
                                byte[] buffer = new byte[320]; // 320 byte  공간의 버퍼 배열
                                int bytesRead; //
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
                ).retrieveFlux(String.class) //  Flux<String>을 반환 받음
                .subscribe(response -> {
                    log.info("Server response: {}", response);
                });

//         클라이언트 코드의 데이터 전송 부분
//        rSocketRequester.route("audioSend")
//                .metadata(metadata, MimeTypeUtils.parseMimeType("message/x.rsocket.routing.v0"))
//                .data(Flux.just("테스트 데이터".getBytes())) // 테스트 데이터 전송
//                .retrieveFlux(String.class)
//                .subscribe(response -> {
//                    log.info("Server response: {}", response);
//                });

    }


}
