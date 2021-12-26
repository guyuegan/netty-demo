package com.neo.io.close;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

@ChannelHandler.Sharable
class ServerHandler extends ChannelDuplexHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String req = (String) msg;
        System.out.println(ctx.name() + " handler receive req: " + req);
        if (req.equalsIgnoreCase("ctx.close."+ctx.name())) {
            ctx.close();
        } else if (req.equalsIgnoreCase("ctx.channel.close."+ctx.name())) {
            ctx.channel().close();
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println(ctx.name()+" close");
        ctx.close(promise);
    }
}
