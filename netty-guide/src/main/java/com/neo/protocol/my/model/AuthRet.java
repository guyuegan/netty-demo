package com.neo.protocol.my.model;

import lombok.Getter;

@Getter
public enum AuthRet {
    FAILURE(-1),
    SUCCESS(0)
    ;

    private final byte code;

    AuthRet(int code) {
        this.code = (byte)code;
    }

    public static boolean isSuccess(byte code) {
        return code == SUCCESS.code;
    }

    public static boolean isFailure(byte code) {
        return code == FAILURE.code;
    }
}
