package com.neo.codec.serialization.java;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            Req req = req(i);
            ctx.write(req);
        }
        ctx.flush();
    }

    private Req req(int i) {
        Req req = new Req();
        req.setId(i);
        req.setUsername("user"+ i);
        req.setAddress("xxx省xxx市xxx区");
        req.setProduct("netty guide");
        return req;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client receive resp: " + msg);
    }

    // todo client channelReadComplete有啥用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("client catch exception: " + cause);
        // 发生异常，关闭链路
        ctx.close();
    }
}
