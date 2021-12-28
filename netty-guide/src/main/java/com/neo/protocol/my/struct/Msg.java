package com.neo.protocol.my.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Msg {
    private Head head;
    private Object body;

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
