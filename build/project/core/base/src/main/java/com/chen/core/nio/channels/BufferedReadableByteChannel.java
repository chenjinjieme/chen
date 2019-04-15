package com.chen.core.nio.channels;

import com.chen.core.nio.ByteBuffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class BufferedReadableByteChannel extends WrappedReadableByteChannel {
    private ByteBuffer buffer;

    public BufferedReadableByteChannel(ReadableByteChannel channel) {
        this(channel, 1024);
    }

    public BufferedReadableByteChannel(ReadableByteChannel channel, int bufferSize) {
        super(channel);
        buffer = ByteBuffer.allocate(bufferSize).position(bufferSize);
    }

    public byte read() throws IOException {
        if (buffer.hasRemaining()) return buffer.get();
        for (; super.read(buffer.clear()) == 0; ) ;
        return buffer.flip().get();
    }

    public int read(ByteBuffer dst) throws IOException {
        var position = dst.position();
        if (buffer.hasRemaining()) ByteBuffers.put(dst, buffer);
        else super.read(dst);
        return dst.position() - position;
    }

    public int read(ByteBuffer dst, int length) throws IOException {
        var position = dst.position();
        if (!buffer.hasRemaining()) {
            super.read(buffer.clear());
            buffer.flip();
        }
        ByteBuffers.put(dst, buffer, length);
        return dst.position() - position;
    }
}
