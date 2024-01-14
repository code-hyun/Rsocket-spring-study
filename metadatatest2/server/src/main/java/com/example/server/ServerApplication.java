package com.example.server;

import jdk.jfr.MetadataDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Map;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Controller
	public static class RSocketController {

		@MessageMapping("request-response")
		public Mono<String> requestResponse(@Payload String data, @Metadata Map<String, Object> metadata) {
			String header = (String) metadata.get("header");
			System.out.println("Received header: " + header);
			return Mono.just("Response to data: '" + data + "', with header: '" + header + "'");
		}
	}
}