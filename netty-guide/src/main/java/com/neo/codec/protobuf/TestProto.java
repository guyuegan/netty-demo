package com.neo.codec.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;

public class TestProto {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        ReqProto.Req req = createReq();
        System.out.println("before encode: " + req);
        ReqProto.Req req2 = decode(encode(req));
        System.out.println("after encode: " + req);
        System.out.println(req.equals(req2));
    }

    private static byte[] encode( ReqProto.Req req) {
        return req.toByteArray();
    }

    private static ReqProto.Req decode(byte[] body) throws InvalidProtocolBufferException {
        return ReqProto.Req.parseFrom(body);
    }

    private static ReqProto.Req createReq() {
        ReqProto.Req.Builder builder = ReqProto.Req.newBuilder();
        builder.setId(1)
                .setUsername("neo")
                .setProduct("netty book")
                .setAddress("x省x市x区");
        return builder.build();
    }
}
