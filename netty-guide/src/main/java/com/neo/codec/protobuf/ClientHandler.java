package com.neo.codec.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.write(req(i));
        }
        ctx.flush();
    }

    private ReqProto.Req req(int i) {
        return ReqProto.Req.newBuilder()
                .setId(i)
                .setUsername("neo" + i)
                .setProduct("book" + i)
                .setAddress("x省x市x区")
                .build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RespProto.Resp resp = (RespProto.Resp) msg;
        System.out.println("client receive resp: " + resp);
    }

    private RespProto.Resp resp(ReqProto.Req req) {
       return RespProto.Resp.newBuilder()
                .setId(req.getId())
                .setCode("0")
                .setMsg("dear " + req.getUsername() + ": your package " + req.getProduct() + " sent to " + req.getAddress())
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.err.println("client catch exception: " + cause);
    }
}
