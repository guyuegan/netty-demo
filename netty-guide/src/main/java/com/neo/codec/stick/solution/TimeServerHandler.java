package com.neo.codec.stick.solution;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

class TimeServerHandler extends ChannelInboundHandlerAdapter {
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String req = (String) msg;
        System.out.println("receive req: " + req + ", counter: " + ++counter);
        if ("now".equalsIgnoreCase(req)) {
            ctx.writeAndFlush(Unpooled.copiedBuffer((LocalDateTime.now().toString()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        } else {
            ctx.writeAndFlush(Unpooled.copiedBuffer(("bad req"+System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("server catch exception: " + cause);
    }
}
