package com.neo.protocol.my.codec;

import org.jboss.marshalling.*;

import java.io.IOException;


class MarshalCodecFactory {

    /**
     * 创建Jboss Marshaller
     */
    protected static Marshaller buildMarshaller() throws IOException {
        final MarshallerFactory marshallerFactory = Marshalling
                .getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return marshallerFactory.createMarshaller(configuration);
    }

    /**
     * 创建Jboss Unmarshaller
     */
    protected static Unmarshaller buildUnMarshaller() throws IOException {
        final MarshallerFactory marshallerFactory = Marshalling
                .getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return marshallerFactory.createUnmarshaller(configuration);
    }
}
