package com.neo.protocol.my.client;

import com.neo.protocol.my.model.Msg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeatSchedule;
    private static final Msg HEART_BEAT_REQ = Msg.heartBeatReq();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Msg resp = (Msg) msg;
        if (resp.isAuthResp()) {
            // 起心跳任务
            heartBeatSchedule = ctx.executor()
                    .scheduleAtFixedRate(new HeartBeatTask(ctx), 5, 5, TimeUnit.SECONDS);
        } else if (resp.isHeartBeatResp()){
            log.info("client receive heat beat resp: {}", resp);
        } else {
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeatSchedule != null) {
            heartBeatSchedule.cancel(true);
        }
        ctx.fireExceptionCaught(cause);
    }

    @AllArgsConstructor
    private static class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        @Override
        public void run() {
            log.info("client send heat beat req: {}", HEART_BEAT_REQ);
            ctx.writeAndFlush(HEART_BEAT_REQ);
        }
    }
}
