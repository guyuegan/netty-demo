package com.neo.codec.serialization.java;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Req req = (Req) msg;
        System.out.println("server receive req: " + req);
        ctx.writeAndFlush(resp(req));
    }

    private Resp resp(Req req) {
        Resp resp = new Resp();
        resp.setId(req.getId());
        resp.setCode("0");
        resp.setMsg("dear " + req.getUsername() + ": book order succeed, 3 days later, sent to " + req.getAddress());
        return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("server catch exception: " + cause);
        // 发生异常，关闭链路
        ctx.close();
    }
}
