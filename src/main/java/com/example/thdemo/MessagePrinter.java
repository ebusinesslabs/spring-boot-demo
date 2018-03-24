package com.example.thdemo;

import org.springframework.stereotype.Component;

@Component
public class MessagePrinter {

    public MessagePrinter() {

    }

    public String sayHello() {
        return "I want to say hello.";
    }
}
