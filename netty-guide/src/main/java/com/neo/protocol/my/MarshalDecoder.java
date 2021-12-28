package com.neo.protocol.my;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

class MarshalDecoder {
    Unmarshaller unmarshaller;

    public MarshalDecoder() throws IOException {
        unmarshaller = MarshalCodecFactory.buildUnMarshaller();
    }

    protected Object decode(ByteBuf in) throws Exception {
        try {
            int objSize = in.readInt();
            ByteBuf buf = in.slice(in.readerIndex(), objSize);
            ChannelBufferByteInput input = new ChannelBufferByteInput(buf);
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + objSize);
            return obj;
        } finally {
            unmarshaller.close();
        }
    }
}
