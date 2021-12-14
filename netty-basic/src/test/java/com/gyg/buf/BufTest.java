package com.gyg.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class BufTest {
    @Test
    public void heapBuf() {
        ByteBuf buf = Unpooled.copiedBuffer("heap buf".getBytes(StandardCharsets.UTF_8));
//        ByteBuf buf = Unpooled.directBuffer();
        if (buf.hasArray()) {
            byte[] array = buf.array();
            int offset = buf.arrayOffset() + buf.readerIndex();
            int length = buf.readableBytes();
            System.out.println(Arrays.toString(array));
            System.out.println(offset);
            System.out.println(length);
        }
        System.out.println(buf);
    }

    @Test
    public void directBuf() {
        // todo 不会自动扩容？
//        ByteBuf buf = Unpooled.copiedBuffer("heap buf".getBytes(StandardCharsets.UTF_8));
        ByteBuf buf = Unpooled.directBuffer();
        buf.writeShort(100);
        if (!buf.hasArray()) {
            int length = buf.readableBytes();
            byte[] array = new byte[length];
            buf.getBytes(buf.readerIndex(), array);
            System.out.println(Arrays.toString(array));
            System.out.println(length);
        }
        System.out.println(buf);
    }

    @Test
    public void expand() {
        // 默认cap: 256 [2^8]
        ByteBuf buf = Unpooled.directBuffer();
        System.out.println(buf);
        for (int i = 0; i < 300; i++) {
            buf.writeByte(i);
        }
        System.out.println(buf);

        // 默认cap: 256 [2^8]
        buf = Unpooled.buffer();
        System.out.println(buf);
        for (int i = 0; i < 300; i++) {
            buf.writeByte(i);
        }
        System.out.println(buf);

        // 查看Unpooled.wrappedBuffer实现可知，其指定了maxCapacity为：array.length
        buf = Unpooled.wrappedBuffer("heap buf".getBytes(StandardCharsets.UTF_8));
        System.out.println(buf);
        buf.writeShort(2);
        System.out.println(buf);

        // 查看Unpooled.copiedBuffer实现可知，底层调用了wrappedBuffer((byte[])array.clone())
        // 说明copiedBuffer只是比wrappedBuffer多了一步clone [todo 即copy不会修改源数据？]
        buf = Unpooled.copiedBuffer("heap buf".getBytes(StandardCharsets.UTF_8));
        System.out.println(buf);
        buf.writeByte(1);
        System.out.println(buf);
    }

    @Test
    public void wrapVsCopy() {
        byte[] bytes = "wrappedBuffer".getBytes(StandardCharsets.UTF_8);
        ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(bytes);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
        System.out.println(wrappedBuffer);
        wrappedBuffer.setChar(0, 'W');
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
        System.out.println(wrappedBuffer);

        byte[] bytes1 = "copiedBuffer".getBytes(StandardCharsets.UTF_8);
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(bytes1);
        System.out.println(new String(bytes1, StandardCharsets.UTF_8));
        System.out.println(wrappedBuffer);
        copiedBuffer.setChar(0, 'C');
        System.out.println(new String(bytes1, StandardCharsets.UTF_8));
        System.out.println(wrappedBuffer);
    }

    @Test
    public void compositeBufJdk() {
        // 使用 ByteBuffer 的复合缓冲区模式
        String header = "header";
        String body = "body";
        ByteBuffer headerBuf = ByteBuffer.allocate(header.length());
        ByteBuffer bodyBuf = ByteBuffer.allocate(body.length());
        headerBuf.put(header.getBytes(StandardCharsets.UTF_8));
        headerBuf.flip();
        bodyBuf.put(body.getBytes(StandardCharsets.UTF_8));
        bodyBuf.flip();
        ByteBuffer[] msg = new ByteBuffer[]{headerBuf, bodyBuf};
        System.out.println(Arrays.toString(msg));

        // 创建复合试图的副本
        ByteBuffer copyMsg = ByteBuffer.allocate(headerBuf.remaining() + bodyBuf.remaining());
        // 如果没有前面的headerBuf.flip()，则这里put的headerBuf是空
        copyMsg.put(headerBuf);
        copyMsg.put(bodyBuf);
        copyMsg.flip();
        System.out.println(copyMsg);
    }

    @Test
    public void compositeBufNetty() {
        // 使用 CompositeByteBuf 的复合缓冲区模式
        CompositeByteBuf msg = Unpooled.compositeBuffer();
        ByteBuf headerBuf = Unpooled.copiedBuffer("header".getBytes(StandardCharsets.UTF_8));
        ByteBuf bodyBuf = Unpooled.directBuffer();
        bodyBuf.writeBytes("body".getBytes(StandardCharsets.UTF_8));
        msg.addComponents(headerBuf, bodyBuf);

        // remove the header (第一个组件)
        msg.removeComponent(0);
        for (ByteBuf buf : msg) {
            System.out.println(buf);
        }
    }

    @Test
    public void compositeBufNettyAccess() {
        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
        int len = compBuf.readableBytes();
        byte[] arr = new byte[len];
        compBuf.getBytes(compBuf.readerIndex(), arr);
        System.out.println(len);
        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void byteBufRandomAccess() {
        ByteBuf buf = Unpooled.copiedBuffer("byteBuf".getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < buf.capacity(); i++) {
            System.out.println((char) buf.getByte(i));
        }
    }

    @Test
    public void byteBufRead() {
        ByteBuf buf = Unpooled.copiedBuffer("byteBuf".getBytes(StandardCharsets.UTF_8));
        while (buf.isReadable()) {
            System.out.println((char) buf.readByte());
        }
    }

    @Test
    public void byteBufWrite() {
        ByteBuf buf = Unpooled.buffer(13);
        System.out.println(buf);
        while (buf.writableBytes() >= 4) {
            buf.writeInt(ThreadLocalRandom.current().nextInt());
        }
        System.out.println(buf);
    }

    @Test
    public void byteBufSlice() {
        ByteBuf buf = Unpooled.copiedBuffer("byteBuf".getBytes(StandardCharsets.UTF_8));
        ByteBuf slice = buf.slice(0, buf.readableBytes());
        System.out.println(slice.toString(StandardCharsets.UTF_8));

        /*
        buf.setByte(0, (byte)'B');  vs  buf.setChar(0, 'B');
        todo 为啥有差异？
         */

        buf.setByte(0, (byte)'B');
        System.out.println(slice.toString(StandardCharsets.UTF_8));
        assert buf.getByte(0) == slice.getByte(0);

        buf.setChar(0, 'B');
        System.out.println(slice.toString(StandardCharsets.UTF_8));
        assert buf.getByte(0) == slice.getByte(0);
    }

    @Test
    public void byteBufCopy() {
        ByteBuf buf = Unpooled.copiedBuffer("byteBuf".getBytes(StandardCharsets.UTF_8));
        ByteBuf copy = buf.copy(0, buf.readableBytes());
        System.out.println(copy.toString(StandardCharsets.UTF_8));

        buf.setByte(0, (byte)'B');
        System.out.println(copy.toString(StandardCharsets.UTF_8));
        assert buf.getByte(0) != copy.getByte(0);

        buf.setChar(0, 'B');
        System.out.println(copy.toString(StandardCharsets.UTF_8));
        assert buf.getByte(0) != copy.getByte(0);
    }

    @Test
    public void byteBufGetSet() {
        ByteBuf buf = Unpooled.copiedBuffer("byteBuf".getBytes(StandardCharsets.UTF_8));
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        System.out.println((char) buf.getByte(0));
        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();

        readerIndex = buf.readerIndex();
        writerIndex = buf.writerIndex();
        buf.setByte(0, (byte) 'B');
        System.out.println((char) buf.getByte(0));
        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();
    }

    @Test
    public void byteBufReadWrite() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes("byteBuf".getBytes(StandardCharsets.UTF_8));
        ByteBuf byteBuf = buf.markReaderIndex();

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        System.out.println((char) buf.readByte());
        assert readerIndex != buf.readerIndex();
        assert writerIndex == buf.writerIndex();

        readerIndex = buf.readerIndex();
        writerIndex = buf.writerIndex();
        buf.writeByte((byte) 'B');
        System.out.println((char) buf.getByte(0));
        assert readerIndex == buf.readerIndex();
        assert writerIndex != buf.writerIndex();

        readerIndex = buf.readerIndex();
        writerIndex = buf.writerIndex();
        System.out.println(buf.toString(StandardCharsets.UTF_8));
        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();

        System.out.println(buf.toString(StandardCharsets.UTF_8));
        byteBuf.resetReaderIndex();
        System.out.println(buf.toString(StandardCharsets.UTF_8));
    }
}
