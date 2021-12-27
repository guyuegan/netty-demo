package com.neo.protocol.my;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

class MsgEncoder extends MessageToMessageEncoder<Msg> {

    MarshalEncoder marshalEncoder;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Msg msg, List<Object> list) throws Exception {
        if (msg == null || msg.getHead() == null) {
            throw new Exception("the encode message is null");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        Head head = msg.getHead();
        sendBuf.writeInt(head.getVersion());
        sendBuf.writeInt(head.getLength());
        sendBuf.writeLong(head.getSessionId());
        sendBuf.writeByte(head.getType());
        sendBuf.writeByte(head.getPriority());
        sendBuf.writeInt(head.getAttachment().size());
        head.getAttachment().forEach((k, v) -> {
            byte[] kBytes = k.getBytes(StandardCharsets.UTF_8);
            sendBuf.writeInt(kBytes.length);
            sendBuf.writeBytes(kBytes);
//            marshalEncoder.encode(v, sendBuf);
        });
        if (msg.getBody() != null) {
//            marshalEncoder.encode(msg.getBody(), sendBuf);
        } else {
            sendBuf.writeInt(0);
            // 偏移4字节，重写length
            sendBuf.setInt(4, sendBuf.readableBytes());
        }
    }
}
