package com.neo.protocol.my.server;

import com.neo.protocol.my.model.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Msg req = (Msg) msg;
        if (req.isHeartBeatReq()) {
            log.info("server receive heart beat req: {}", req);
            Msg resp = Msg.heartBeatResp();
            log.info("server send heart beat resp: {}", resp);
            ctx.writeAndFlush(resp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
