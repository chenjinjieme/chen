package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.NonWritableChannelException;

public class ByteBufferChannel implements ByteChannel {
    private final ByteBuffer buffer;
    private boolean close;

    public ByteBufferChannel(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBufferChannel(byte[] bytes) {
        this(ByteBuffer.wrap(bytes));
    }

    public ByteBufferChannel(byte b) {
        this(new byte[]{b});
    }

    public int read(ByteBuffer dst) {
        if (!buffer.hasRemaining()) return -1;
        var read = Math.min(buffer.remaining(), dst.remaining());
        for (var i = read; i > 0; i--) dst.put(buffer.get());
        return read;
    }

    public int write(ByteBuffer src) {
        throw new NonWritableChannelException();
    }

    public boolean isOpen() {
        return !close;
    }

    public void close() {
        close = true;
    }
}
