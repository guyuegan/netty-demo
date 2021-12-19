package com.neo.io.buf;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferTest {
    @Test
    public void testWriteRead() {
        String s = "netty权威指南";
        byte[] in = s.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(in.length);
        byteBuffer.put(in);
        byteBuffer.flip();
        byte[] out = new byte[byteBuffer.remaining()];
        byteBuffer.get(out);
        System.out.println(new String(out, StandardCharsets.UTF_8));
    }

    @Test
    public void testWriteReadNoFlip() {
        String s = "netty权威指南";
        byte[] in = s.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(in.length);
        byteBuffer.put(in);
//        byteBuffer.flip();
        byte[] out = new byte[byteBuffer.remaining()];
        byteBuffer.get(out);
        System.out.println(new String(out, StandardCharsets.UTF_8));
    }
}
