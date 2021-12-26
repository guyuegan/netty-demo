package com.neo.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

class ServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final String[] poems = {
            "只要功夫深，铁棒磨成针",
            "旧时王谢堂前燕，飞入寻常百姓家",
            "一寸光阴一寸金，寸金难买寸光阴",
            "孤舟蓑笠翁，独钓寒江雪"
    };

    private String nextPoem() {
        return poems[ThreadLocalRandom.current().nextInt(poems.length)];
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String req = packet.content().toString(StandardCharsets.UTF_8);
        System.out.println("server receive req: " + req);
        if ("query poem".equals(req)) {
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("resp poem" + nextPoem(), StandardCharsets.UTF_8),
                    packet.sender()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
