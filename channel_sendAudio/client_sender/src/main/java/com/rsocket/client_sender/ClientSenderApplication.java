package com.rsocket.client_sender;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientSenderApplication implements CommandLineRunner {
    private final ClientRun clientRun;

    public ClientSenderApplication(ClientRun clientRun){
        this.clientRun = clientRun;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientSenderApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        clientRun.run();
    }
}
