package com.chen.core.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class WrappedReadableByteChannel extends WrappedChannel<ReadableByteChannel> implements ReadableByteChannel {
    public WrappedReadableByteChannel(ReadableByteChannel channel) {
        super(channel);
    }

    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }
}
