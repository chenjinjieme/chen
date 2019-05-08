package com.chen.core.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class BufferedWritableByteChannel implements WritableByteChannel {
    private WritableByteChannel channel;
    private ByteBuffer buffer;

    public BufferedWritableByteChannel(WritableByteChannel channel) {
        this(channel, 8192);
    }

    public BufferedWritableByteChannel(WritableByteChannel channel, int bufferSize) {
        this.channel = channel;
        buffer = ByteBuffer.allocate(bufferSize);
    }

    public BufferedWritableByteChannel write(byte b) throws IOException {
        if (!buffer.hasRemaining()) {
            channel.write(buffer.flip());
            buffer.clear();
        }
        buffer.put(b);
        return this;
    }

    public BufferedWritableByteChannel writeInt(int i) throws IOException {
        write((byte) (i >>> 24)).write((byte) (i >>> 16)).write((byte) (i >>> 8)).write((byte) i);
        return this;
    }

    public BufferedWritableByteChannel writeLong(long l) throws IOException {
        write((byte) (l >>> 56)).write((byte) (l >>> 48)).write((byte) (l >>> 40)).write((byte) (l >>> 32)).write((byte) (l >>> 24)).write((byte) (l >>> 16)).write((byte) (l >>> 8)).write((byte) l);
        return this;
    }

    public int write(ByteBuffer src) throws IOException {
        flush();
        var position = src.position();
        channel.write(src);
        return src.position() - position;
    }

    public BufferedWritableByteChannel flush() throws IOException {
        if (buffer.hasRemaining()) {
            channel.write(buffer.flip());
            buffer.clear();
        }
        return this;
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public void close() throws IOException {
        flush();
        channel.close();
    }
}
