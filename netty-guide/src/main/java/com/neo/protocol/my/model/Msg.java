package com.neo.protocol.my.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Msg {
    private Head head;
    private Object body;

    public boolean isHeartBeatResp() {
        return head != null && head.getType() == MsgType.HEART_BEAT_RESP.getCode();
    }

    public boolean isHeartBeatReq() {
        return head != null && head.getType() == MsgType.HEART_BEAT_REQ.getCode();
    }

    public static Msg heartBeatResp() {
        Head head = new Head();
        head.setType(MsgType.HEART_BEAT_RESP.getCode());
        return new Msg(head, null);
    }

    public static Msg heartBeatReq() {
        Head head = new Head();
        head.setType(MsgType.HEART_BEAT_REQ.getCode());
        return new Msg(head, null);
    }

    public boolean isAuthResp() {
        return head != null && head.getType() == MsgType.AUTH_RESP.getCode();
    }

    public boolean isAuthReq() {
        return head != null && head.getType() == MsgType.AUTH_REQ.getCode();
    }

    public static Msg authReq() {
        Head head = new Head();
        head.setType(MsgType.AUTH_REQ.getCode());
        return new Msg(head, null);
    }

    public static Msg authResp(byte ret) {
        Head head = new Head();
        head.setType(MsgType.AUTH_RESP.getCode());
        return new Msg(head, ret);
    }

    public static Msg authSuccessResp() {
        return authResp(AuthRet.SUCCESS.getCode());
    }

    public static Msg authFailureResp() {
        return authResp(AuthRet.FAILURE.getCode());
    }
}
