package com.neo.protocol.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handShaker;
    private static Map<String, Channel> clientMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive: " + LocalDateTime.now());
        clientMap.put(ctx.channel().remoteAddress().toString(), ctx.channel());
        WebSocketServerHandler.task();

        /* 这个发送不会被客户端成功接收，因为还没建立起webSocket连接
        channelActive: 2021-12-26T16:42:24.372
        channelRead0#FullHttpRequest: 2021-12-26T16:42:24.416
        channelReadComplete: 2021-12-26T16:42:24.462
        channelRead0#WebSocketFrame: 2021-12-26T16:42:28.756
        server receive req: Netty WebSocket实战
        channelReadComplete: 2021-12-26T16:42:28.757
         */
        // todo 如果用write会导致和前端的连接断开，为啥？
        ctx.channel().writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress() + "hi, i'm server, let's communicating"));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // http接入
        if (msg instanceof FullHttpRequest) {
            System.out.println("channelRead0#FullHttpRequest: " + LocalDateTime.now());
            handleHttpReq(ctx, (FullHttpRequest)msg);
        }
        // web socket接入
        if (msg instanceof WebSocketFrame) {
            System.out.println("channelRead0#WebSocketFrame: " + LocalDateTime.now());
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 关闭链路指令
        if (frame instanceof CloseWebSocketFrame) {
            handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 当前只支持文本，不支持二进制
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
        }
        String req = ((TextWebSocketFrame) frame).text();
        System.out.println("server receive req: " + req);
        ctx.channel().write(new TextWebSocketFrame(req + ", welcome to use web socket, now: " + LocalDateTime.now()));
    }

    private void handleHttpReq(ChannelHandlerContext ctx, FullHttpRequest req) {
        // http解码失败，不是升级web socket请求，都返回400
        if (req.decoderResult().isFailure()
                || !"websocket".equals(req.headers().get("Upgrade"))) {
            sendHttpResp(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 构造握手响应返回（本机测试）: 工厂模式，根据不同webSocket版本处理
        // "ws://127.0.0.1:8888/webSocket"
        String webSocketUrl = "ws://" + req.headers().get("host") + req.uri();
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketUrl, null, false);
        handShaker = wsFactory.newHandshaker(req);
        if (handShaker != null) {
            // handler动态增删改
            handShaker.handshake(ctx.channel(), req);
        } else {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }
    }

    private void sendHttpResp(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp) {
        if (resp.status() != HttpResponseStatus.OK) {
            ByteBuf buf = Unpooled.copiedBuffer(resp.status().toString(), StandardCharsets.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(resp, resp.content().readableBytes());
        }
        // 非keep-alive, 关闭连接
        ChannelFuture future = ctx.channel().writeAndFlush(resp);
        if (!HttpUtil.isKeepAlive(req) || resp.status() != HttpResponseStatus.OK) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete: " + LocalDateTime.now());
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.err.println("server catch exception: " + cause);
        ctx.close();
    }

    public static void task() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        for (final Channel channel : clientMap.values()) {
            executor.scheduleAtFixedRate(() -> channel.writeAndFlush(new TextWebSocketFrame("hello from server"+LocalDateTime.now())), 5, 5, TimeUnit.SECONDS);
        }
    }



}
