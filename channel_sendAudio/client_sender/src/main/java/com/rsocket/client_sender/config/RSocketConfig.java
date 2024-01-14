package com.rsocket.client_sender.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class RSocketConfig {
    @Bean
    public RSocketRequester getRSocketRequester(RSocketStrategies rSocketStrategies){ // rsocketStrategies : 서버와 통신 시 JSON으로 값을 담아 통신하는데 필요한 인코더와 디코더
//        RSocketStrategies rSocketStrategies = RSocketStrategies.builder()
//                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
//                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
//                .build();

        return RSocketRequester.builder() // Rsocket server에 연결해 클라이언트 생성하기 위한 빌더
                // 서버 재연결 설정
                .rsocketConnector(connector -> connector.reconnect(Retry.backoff(10, Duration.ofMillis(500)))) // 5초에 한번 재연결 최대 10번까지
                .rsocketStrategies(rSocketStrategies)
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON) // JSON 통신 타입
                .tcp("localhost", 7000); // tcp 통신
                // websocket 통신을 위한 설정도 할 수 있다.
//        RSocketRequester.builder().metadataMimeType()
    }
 }

/* RSocketRequester.builder().tcp() : tcp(host, port)
 *  RSocketRequester.builder().metadataMimeType() :
 *
 */
