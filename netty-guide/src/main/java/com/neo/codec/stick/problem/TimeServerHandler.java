package com.neo.codec.stick.problem;

import io.netty.buffer.ByteBuf;
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
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String req = new String(bytes, StandardCharsets.UTF_8);
        req = req.substring(0, req.lastIndexOf(System.lineSeparator()));
        System.out.println("server receive req: " + req + ", the counter is: " + ++counter);
        if ("now".equalsIgnoreCase(req)) {
            ctx.writeAndFlush(Unpooled.copiedBuffer((LocalDateTime.now().toString()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        } else {
            ctx.writeAndFlush(Unpooled.copiedBuffer("bad req\n".getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        System.out.println("server catch exception: " + cause);
    }
}
