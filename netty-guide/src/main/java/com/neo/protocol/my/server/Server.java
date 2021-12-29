package com.neo.protocol.my.server;

import com.neo.protocol.my.codec.MsgDecoder;
import com.neo.protocol.my.codec.MsgEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import static com.neo.protocol.my.Constant.*;

class Server {

    public static void main(String[] args) throws InterruptedException {
        bind();
    }

    private static void bind() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new MsgDecoder(1024 * 1024, 4, 4))
                                    .addLast(new ReadTimeoutHandler(50))
                                    .addLast(new MsgEncoder())
                                    .addLast(new HeartBeatRespHandler())
                                    .addLast(new AuthRespHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            ChannelFuture future = serverBootstrap.bind(REMOTE_IP, REMOTE_PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
