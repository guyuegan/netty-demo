package com.neo.protocol.my.model;

import lombok.Getter;

@Getter
public enum MsgType {
    /**
     * 0-业务请求
     */
    BIZ_REQ(0),
    /**
     * 1-业务响应
     */
    BIZ_RESP(1),
    /**
     * 2-业务ONE WAY消息（既是请求又是响应消息）
     */
    BIZ_ONE_WAY(2),

    /**
     * 3-握手请求
     */
    AUTH_REQ(3),
    /**
     * 4-握手响应
     */
    AUTH_RESP(4),

    /**
     * 5-心跳请求
     */
    HEART_BEAT_REQ(5),
    /**
     * 6-心跳应答
     */
    HEART_BEAT_RESP(6),
    ;

    private final byte code;

    MsgType(int type) {
        this.code = (byte)type;
    }
}


