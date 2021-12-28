package com.neo.io.file;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

// https://juejin.cn/post/6844904186690142222
public class FileChannelDemo {
    public static void main(String[] args) throws Exception {
        testGetFile();
        testRandomAccessFile();
    }

    private static void testRandomAccessFile() throws Exception {
        // FileChannel无法直接打开，需要通过InputStream, OutputStream或RandomAccessFile来获取
        String path = FileChannelDemo.class.getClassLoader().getResource("test.txt").getPath();
        RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        String text = "FileChannel无法直接打开，需要通过InputStream, OutputStream或RandomAccessFile来获取";
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.put(text.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        channel.write(buffer);
    }

    private static void testGetFile() throws Exception {
        /*
        file:/Users/neo/IdeaProjects/netty-demo/netty-basic/target/classes/com/neo/io/file/
        file:/Users/neo/IdeaProjects/netty-demo/netty-basic/target/classes/
        file:/Users/neo/IdeaProjects/netty-demo/netty-basic/target/classes/
        null
         */
        System.out.println(FileChannelDemo.class.getResource(""));
        System.out.println(FileChannelDemo.class.getResource("/"));

        System.out.println(FileChannelDemo.class.getClassLoader().getResource(""));
        System.out.println(FileChannelDemo.class.getClassLoader().getResource("/"));
    }
}
