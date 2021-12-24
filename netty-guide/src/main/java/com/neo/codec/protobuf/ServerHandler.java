package com.neo.codec.protobuf;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ReqProto.Req req = (ReqProto.Req) msg;
        System.out.println("server receive req: " + req);
        ctx.writeAndFlush(resp(req));
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
        System.err.println("server catch exception: " + cause);
    }
}
