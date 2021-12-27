package com.neo.file;

import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.File;
import java.io.RandomAccessFile;

class ServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String req) throws Exception {
        req = req.replace(System.lineSeparator(), "");
        File file = new File(req);
        if (file.exists()) {
            if (!file.isFile()) {
                ctx.writeAndFlush("not a file: " + file + System.lineSeparator());
                return;
            }

            ctx.write(file + " " + file.length() + System.lineSeparator());
            RandomAccessFile randomAccessFile = new RandomAccessFile(req, "r");
            // netty FileRegion
            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
            ctx.write(region);
            ctx.writeAndFlush(System.lineSeparator());
            randomAccessFile.close();
        } else {
            ctx.writeAndFlush("file not found: " + file + System.lineSeparator());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

