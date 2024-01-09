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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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
        Path path = Path.of("D:\\01. project\\Rsocket-spring-study\\channel_sendAudio\\client_sender\\src\\main\\resources\\static\\audio\\test.wav");
        File file = path.toFile();
//        byte[] buffer = new byte[320];
//        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))

        String headerJson = new ObjectMapper().writeValueAsString(test);
        ByteBuf metadata = ByteBufAllocator.DEFAULT.buffer().writeBytes(headerJson.getBytes(StandardCharsets.UTF_8));

        System.out.println(headerJson);
        System.out.println(metadata);


        rSocketRequester.route("audioSend")
                .metadata(metadata, MimeTypeUtils.parseMimeType("message/x.rsocket.routing.v0"))
                .data(

                )



    }
}
