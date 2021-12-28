package com.neo.protocol.my.handler;

import com.neo.protocol.my.struct.AuthRet;
import com.neo.protocol.my.struct.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthReqHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Msg.authReq());
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
