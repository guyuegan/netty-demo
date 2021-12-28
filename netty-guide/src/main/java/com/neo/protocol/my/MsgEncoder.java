package com.neo.protocol.my;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

class MsgEncoder extends MessageToByteEncoder<Msg> {

    MarshalEncoder marshalEncoder;

    @Override
    protected void encode(ChannelHandlerContext ctx, Msg msg, ByteBuf out) throws Exception {
        if (msg == null || msg.getHead() == null) {
            throw new Exception("the encode message is null");
        }
        Head head = msg.getHead();
        out.writeInt(head.getVersion());
        out.writeInt(head.getLength());
        out.writeLong(head.getSessionId());
        out.writeByte(head.getType());
        out.writeByte(head.getPriority());
        out.writeInt(head.getAttachment().size());
        for (Map.Entry<String, Object> entry : head.getAttachment().entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            out.writeInt(keyBytes.length);
            out.writeBytes(keyBytes);
            marshalEncoder.encode(val, out);
        }
        if (msg.getBody() != null) {
            marshalEncoder.encode(msg.getBody(), out);
        } else {
            out.writeInt(0);
            // 偏移4字节，重写length
            out.setInt(4, out.readableBytes()-8);
        }
    }
}