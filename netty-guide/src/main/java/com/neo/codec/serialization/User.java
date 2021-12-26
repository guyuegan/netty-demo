package com.neo.codec.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

class User implements Serializable {
    private int id;
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // 基于ByteBuffer的通用二进制编解码
    public byte[] commonEncode() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] value = username.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(id);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    // JDK原生序列化机制
    public byte[] jdkEncode() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(bos)) {
            os.writeObject(this);
            os.flush();
            return bos.toByteArray();
        }
    }

    // 基于ByteBuffer的通用二进制编解码: 性能测试
    public byte[] commonEncodePerformance(ByteBuffer buffer) {
        buffer.clear();
        byte[] value = username.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(id);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    // JDK原生序列化机制: 性能测试 todo 必须每次新建bos, os才有效？
    public byte[] jdkEncodePerformance(ByteArrayOutputStream bos, ObjectOutputStream os) throws IOException {
        os.writeObject(this);
        os.flush();
        return bos.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        serializationSize();
        serializationPerformance();
    }

    private static void serializationSize() throws IOException {
        User user = new User();
        user.setId(100);
        user.setUsername("Welcome to Netty");

        System.out.println("The jdk serializable length is: " + user.jdkEncode().length);
        System.out.println("The byte array serializable length is: " + user.commonEncode().length);
    }

    private static void serializationPerformance() throws IOException {
        User user = new User();
        user.setId(100);
        user.setUsername("Welcome to Netty");

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            user.jdkEncode();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("The jdk serializable cost time is: " + (endTime - startTime) + " ms");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            user.commonEncodePerformance(buffer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("The byte array serializable cost time is: " + (endTime - startTime) + " ms");
    }

}
