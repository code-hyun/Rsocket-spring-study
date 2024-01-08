package com.rsocket.server.repository;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    private String username;
    private String message;

    public Message(String username, String message){
        this.username = username;
        this.message = message;
    }
}
