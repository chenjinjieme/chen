package com.chen.core.nio.channels;

import com.chen.core.nio.ByteBuffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class BufferedReadableByteChannel implements ReadableByteChannel {
    private ReadableByteChannel channel;
    private ByteBuffer buffer;

    public BufferedReadableByteChannel(ReadableByteChannel channel) {
        this(channel, 8192);
    }

    public BufferedReadableByteChannel(ReadableByteChannel channel, int bufferSize) {
        this.channel = channel;
        buffer = ByteBuffer.allocate(bufferSize).limit(0);
    }

    public byte read() throws IOException {
        if (buffer.hasRemaining()) return buffer.get();
        for (; channel.read(buffer.clear()) == 0; ) ;
        return buffer.flip().get();
    }

    public int readInt() throws IOException {
        return read() << 24 | (read() & 0xff) << 16 | (read() & 0xff) << 8 | (read() & 0xff);
    }

    public long readLong() throws IOException {
        return (read() & 0xffL) << 56 | (read() & 0xffL) << 48 | (read() & 0xffL) << 40 | (read() & 0xffL) << 32 | (read() & 0xff) << 24 | (read() & 0xff) << 16 | (read() & 0xff) << 8 | (read() & 0xff);
    }

    public int read(ByteBuffer dst) throws IOException {
        var position = dst.position();
        if (buffer.hasRemaining()) ByteBuffers.put(dst, buffer);
        if (dst.hasRemaining()) channel.read(dst);
        return dst.position() - position;
    }

    public int read(ByteBuffer dst, int length) throws IOException {
        var buffer = ByteBuffer.allocate(length);
        var read = read(buffer);
        dst.put(buffer.flip());
        return read;
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public void close() throws IOException {
        channel.close();
    }
}
