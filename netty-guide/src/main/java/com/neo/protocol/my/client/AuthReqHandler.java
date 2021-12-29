package com.neo.protocol.my.client;

import com.neo.protocol.my.model.AuthRet;
import com.neo.protocol.my.model.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AuthReqHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Msg authReq = Msg.authReq();
        log.info("login req: {}", authReq);
        ctx.writeAndFlush(authReq);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Msg resp = (Msg) msg;
        // 如果是握手应答消息，判断是否认证成功
        if (resp.isAuthResp()) {
            byte authRet = (byte) resp.getBody();
            if (AuthRet.isFailure(authRet)) {
                // 握手失败，关闭连接
                log.error("login fail: " + msg);
                ctx.close();
            } else {
                log.info("login success: " + msg);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
