package com.neo.protocol.my.codec;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

@Getter
class ChannelBufferByteOutput implements ByteOutput {
    private final ByteBuf buffer;

    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int i) throws IOException {
        buffer.writeInt(i);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        buffer.writeBytes(bytes);
    }

    @Override
    public void write(byte[] bytes, int srcIdx, int length) throws IOException {
        buffer.writeBytes(bytes, srcIdx, length);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }
}
