package com.neo.protocol.my.handler;

import com.neo.protocol.my.struct.AuthRet;
import com.neo.protocol.my.struct.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class AuthRespHandler extends ChannelInboundHandlerAdapter {
    private Set<String> loginNodes = new HashSet<>();
    private List<String> whiteList = Arrays.asList("127.0.0.1");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Msg req = (Msg) msg;
        // 如果是握手请求，处理，其他消息透传
        Msg authResp;
        if (req.isAuthReq()) {
            // 重复登录检查
            String nodeIdx = ctx.channel().remoteAddress().toString();
            if (!isRepeatLogin(nodeIdx) && isLegalIp(ctx)) {
                loginNodes.add(nodeIdx);
                authResp = Msg.authSuccessResp();
            } else {
                authResp = Msg.authFailureResp();
            }
            log.info("login resp: {}", authResp);
            ctx.writeAndFlush(authResp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private boolean isLegalIp(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = socketAddress.getAddress().getHostAddress();
        return whiteList.contains(ip);
    }

    private boolean isRepeatLogin(String nodeIdx) {
        return loginNodes.contains(nodeIdx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // [注意异常处理]: channel关闭前，清除缓存，重连才不会出现'重复登录'
        loginNodes.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
