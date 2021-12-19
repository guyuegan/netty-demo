package com.neo.io.nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;

class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMsg;

    public TimeClientHandler() {
        byte[] bytes = "now".getBytes(StandardCharsets.UTF_8);
        firstMsg = Unpooled.buffer(bytes.length);
        firstMsg.writeBytes(bytes);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("send req: " + firstMsg.toString(StandardCharsets.UTF_8));
        ctx.writeAndFlush(firstMsg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("receive resp: " + buf.toString(StandardCharsets.UTF_8));
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("client catch exception");
    }
}
