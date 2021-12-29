package com.neo.protocol.my.client;

import com.neo.protocol.my.codec.MsgDecoder;
import com.neo.protocol.my.codec.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.neo.protocol.my.Constant.*;

class Client {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        connect();
    }

    private static void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new MsgDecoder(1024 * 1024, 4, 4))
                                    .addLast(new ReadTimeoutHandler(50))
                                    .addLast(new MsgEncoder())
                                    .addLast(new HeartBeatReqHandler())
                                    .addLast(new AuthReqHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(REMOTE_IP, REMOTE_PORT), new InetSocketAddress(LOCAL_IP, LOCAL_PORT)).sync();
            // 当对应的channel关闭的时候，就会返回对应的channel。
            // Returns the ChannelFuture which will be notified when this channel is closed. This method always returns the same future instance.
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
