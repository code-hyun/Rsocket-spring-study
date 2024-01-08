package com.rsocket.server.repository;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    private String userName;
    private String message;

    public Message(String userName, String message){
        this.userName = userName;
        this.message = message;
    }
}
