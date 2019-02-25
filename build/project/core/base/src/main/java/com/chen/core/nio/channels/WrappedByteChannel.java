package com.chen.core.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class WrappedByteChannel extends WrappedChannel<ByteChannel> implements ByteChannel {
    public WrappedByteChannel(ByteChannel channel) {
        super(channel);
    }

    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    public int write(ByteBuffer src) throws IOException {
        return channel.write(src);
    }
}
