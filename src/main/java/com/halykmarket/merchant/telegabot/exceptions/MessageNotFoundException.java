package com.halykmarket.merchant.telegabot.exceptions;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(String s) {
        super(s);
    }
}
