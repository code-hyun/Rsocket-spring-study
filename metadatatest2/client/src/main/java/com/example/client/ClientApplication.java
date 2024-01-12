package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {
	private final ClientRun clientRun;

	public ClientApplication(ClientRun clientRun) {
		this.clientRun = clientRun;
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		clientRun.run();
	}
}
