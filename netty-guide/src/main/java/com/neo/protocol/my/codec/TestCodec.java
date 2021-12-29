package com.neo.protocol.my.codec;

import com.neo.protocol.my.model.Head;
import com.neo.protocol.my.model.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class TestCodec {

    MarshalEncoder marshalEncoder;
    MarshalDecoder marshalDecoder;

    public TestCodec() throws IOException {
        marshalEncoder = new MarshalEncoder();
        marshalDecoder = new MarshalDecoder();
    }

    public Msg getMessage() {
        Head head = new Head();
        head.setLength(123);
        head.setSessionId(99999);
        head.setType((byte) 1);
        head.setPriority((byte) 7);
        Map<String, Object> attachment = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            attachment.put("city --> " + i, "neo " + i);
        }
        head.setAttachment(attachment);

        return new Msg(head, "abcdefg-----------------------AAAAAA");
    }

    public ByteBuf encode(Msg msg) throws Exception {
        ByteBuf sendBuf = Unpooled.buffer();
        Head head = msg.getHead();
        sendBuf.writeInt((head.getVersion()));
        sendBuf.writeInt((head.getLength()));
        sendBuf.writeLong((head.getSessionId()));
        sendBuf.writeByte((head.getType()));
        sendBuf.writeByte((head.getPriority()));
        sendBuf.writeInt((head.getAttachment().size()));
        for (Map.Entry<String, Object> param : head.getAttachment().entrySet()) {
            String key = param.getKey();
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            sendBuf.writeInt(keyBytes.length);
            sendBuf.writeBytes(keyBytes);
            Object value = param.getValue();
            marshalEncoder.encode(value, sendBuf);
        }

        if (msg.getBody() != null) {
            marshalEncoder.encode(msg.getBody(), sendBuf);
        } else {
            sendBuf.writeInt(0);
            sendBuf.setInt(4, sendBuf.readableBytes());
        }
        return sendBuf;
    }

    public Msg decode(ByteBuf in) throws Exception {
        Head head = new Head();
        head.setVersion(in.readInt());
        head.setLength(in.readInt());
        head.setSessionId(in.readLong());
        head.setType(in.readByte());
        head.setPriority(in.readByte());

        int attachmentSize = in.readInt();
        if (attachmentSize > 0) {
            Map<String, Object> attachment = new HashMap<>(attachmentSize);
//            for (int i = 0; i < attachmentSize; i++) {
//                int keySize = in.readInt();
//                byte[] keyArray = new byte[keySize];
//                in.readBytes(keyArray);
//                attachment.put(new String(keyArray, StandardCharsets.UTF_8), marshalDecoder.decode(in));
//            }
            for (int i = 0; i < attachmentSize; i++) {
                String key = in.readBytes(in.readInt()).toString(StandardCharsets.UTF_8);
                Object val = marshalDecoder.decode(in);
                attachment.put(key, val);
            }
            head.setAttachment(attachment);
        }

        Msg message = new Msg(head, null);
        if (in.readableBytes() > 4) {
            message.setBody(marshalDecoder.decode(in));
        }
        return message;
    }

    // todo 为什么这里不需要LengthFieldBasedFrameDecoder， 因为是本地，没有网络导致的粘包半包
    public static void main(String[] args) throws Exception {
        TestCodec test = new TestCodec();
        Msg msg = test.getMessage();
        System.out.println(msg + "[body ] " + msg.getBody());
        System.out.println("-------------------------------------------------");

        for (int i = 0; i < 5; i++) {
            ByteBuf buf = test.encode(msg);
            Msg decodeMsg = test.decode(buf);
            System.out.println(decodeMsg + "[body ] " + decodeMsg.getBody());
        }
    }

}
