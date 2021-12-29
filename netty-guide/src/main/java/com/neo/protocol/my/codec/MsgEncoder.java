package com.neo.protocol.my.codec;

import com.neo.protocol.my.model.Head;
import com.neo.protocol.my.model.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MsgEncoder extends MessageToByteEncoder<Msg> {

    MarshalEncoder marshalEncoder;

    public MsgEncoder() throws IOException {
        this.marshalEncoder = new MarshalEncoder();
    }

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
            // todo 写这个int干啥？
            out.writeInt(0);
        }

        /**【坑】之前放在上面的else：导致client解码AuthResp失败；但server解码AuthReq成功
         *
         * 原因：
         * client发送的AuthReq消息没有body, 进入上面的else, length计算正确
         * server发送的AuthResp消息有body, 进入上面的if, length计算错误
         *
         * 日志：
         * client send login req: Msg(head=Head(version=-1410399999, length=0, sessionId=0, type=3, priority=0, attachment={}), body=null)
         *
         * server receive login req: Msg(head=Head(version=-1410399999, length=18, sessionId=0, type=3, priority=0, attachment={}), body=null)
         * server send login resp: Msg(head=Head(version=-1410399999, length=0, sessionId=0, type=4, priority=0, attachment={}), body=0)
         *
         * 报错：
         * io.netty.handler.codec.DecoderException: java.lang.IndexOutOfBoundsException: readerIndex(8) + length(8) exceeds writerIndex(8): PooledSlicedByteBuf(ridx: 8, widx: 8, cap: 8/8, unwrapped: PooledUnsafeDirectByteBuf(ridx: 8, widx: 101, cap: 2048))
         */
        // 偏移4字节，重写length
        out.setInt(4, out.readableBytes()-8);
    }
}
