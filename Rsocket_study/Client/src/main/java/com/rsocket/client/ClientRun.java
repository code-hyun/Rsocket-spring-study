package com.rsocket.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Component
public class ClientRun {
    // RSocketRequester : Rsocket 연결을 추상화 하고 Rsocket 통신을 쉽게 수행 할 수 있도록 도와 주는 클래스
    // 주로 서버와 클라이언트 간 메시지를 주고 받을 때 사용한다.
    private final RSocketRequester rSocketRequester;

    @Autowired
    public ClientRun(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    public void run() {
        try {
            // 예제: request-response 메소드 호출
            requestResponse();
            monoReqRes();

            // 예제: fire-and-forget 메소드 호출
//            fireAndForget();

            // 예제: stream 메소드 호출
//            stream();

            // 예제: channel 메소드 호출
//            channel();

            // 프로그램 종료 전에 잠시 대기
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // request-response 메소드
    private void requestResponse() {
        // 서버로의 request-response 요청
                // rSocketRequest.route - 경로 지정
        String response = rSocketRequester.route("req-res")
                // .data() 보낼 메시지의 데이터
                .data(new Message("superpil", "client"))
                // Mono 사용하여 단일 응답(여기서는 String 형태로 응답 지정)
                .retrieveMono(String.class)
                // Mono 값에서 동기적으로 블록하고, 해당 값 반환
                .block();
        System.out.println("Response from server: " + response);
    }
    private void monoReqRes() {
        String str = "abcdefg!@#$%^";
        Random r = new Random();
        String id = "";
        for (int i = 0; i<10; i++){
            id += str.charAt(r.nextInt(12));
        }
        // 서버로의 Mono request-response 요청
        String response = rSocketRequester.route("mono-req-res")
                .data(id)
                .retrieveMono(String.class)
                .block();
        System.out.println("req-res Mono test : " + response);
    }

    // fire-and-forget 메소드
    private void fireAndForget() {
        // 서버로의 fire-and-forget 요청
        rSocketRequester.route("fire-and-forget")
                .data(new Message("superpil", "client"))
                .send()
                .subscribe();
        System.out.println("Fire-and-forget request sent to server.");
    }

    // stream 메소드
    private void stream() {
        // 서버로의 stream 요청
        rSocketRequester.route("stream")
                .data(new Message("superpil", "client"))
                .retrieveFlux(String.class)
                .subscribe(response -> System.out.println("Received from stream: " + response));
    }

    // channel 메소드
    // ... (이전 코드 생략)

    // channel 메소드
    private void channel() {
        // 채널 요청을 보낼 Flux 생성 (예: 간격 설정)
        Flux<Long> settingsFlux = Flux.interval(Duration.ofSeconds(1));

        // RSocket 채널 생성 및 설정된 간격으로 메시지 전송
        rSocketRequester.route("channel")
                .data(settingsFlux, Long.class)  // Flux<Long>으로 변경
                .retrieveFlux(Message.class)
                .subscribe(response -> System.out.println("Received from channel: " + response));

        // 잠시 기다림
        try {
            Thread.sleep(30000); // 예: 30초 동안 실행 유지
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
