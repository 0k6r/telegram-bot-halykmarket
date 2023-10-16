package com.halykmarket.merchant.telegabot.exceptions;

public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(Exception e) {
        super(e);
    }
}
