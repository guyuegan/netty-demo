package com.neo.protocol.my.codec;

import com.neo.protocol.my.struct.Head;
import com.neo.protocol.my.struct.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class MsgDecoder extends LengthFieldBasedFrameDecoder {

    MarshalDecoder marshalDecoder;

    public MsgDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.marshalDecoder = new MarshalDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        Head head = new Head();
        head.setVersion(frame.readInt());
        head.setLength(frame.readInt());
        head.setSessionId(frame.readLong());
        head.setType(frame.readByte());
        head.setPriority(frame.readByte());
        int attachmentSize = frame.readInt();
        /*
        for (Map.Entry<String, Object> entry : head.getAttachment().entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            out.writeInt(keyBytes.length);
            out.writeBytes(keyBytes);
            marshalEncoder.encode(val, out);
        }
         */
        if (attachmentSize > 0) {
            Map<String, Object> attachment = new HashMap<>(attachmentSize);
            for (int i = 0; i < attachmentSize; i++) {
                // todo frame.readBytes产生的ByteBuf，要手动释放?
                String key = frame.readBytes(frame.readInt()).toString(StandardCharsets.UTF_8);
                Object val = marshalDecoder.decode(frame);
                attachment.put(key, val);
            }
            head.setAttachment(attachment);
        }
        Msg msg = new Msg();
        msg.setHead(head);
        // 除了body长度还有可读字节，才进行body解码
        if (frame.readableBytes() > 4) {
            msg.setBody(marshalDecoder.decode(frame));
        }
        return msg;
    }
}
