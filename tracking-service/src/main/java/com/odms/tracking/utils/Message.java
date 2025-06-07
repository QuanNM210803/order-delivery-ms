package com.odms.tracking.utils;

import lombok.Getter;

@Getter
public enum Message {


    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }
}
