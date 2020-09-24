package com.chen.file.image.png;

import com.chen.core.nio.channels.ByteBufferChannel;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

class RawChunk implements Chunk {
    private final ByteBuffer buffer;

    private RawChunk(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    static Chunk parse(ByteBuffer buffer) {
        var position = buffer.position();
        var length = buffer.getInt() + 12;
        return new RawChunk(ByteBuffer.wrap(buffer.position(position + length).array(), position, length));
    }

    public ByteChannel channel() {
        return new ByteBufferChannel(buffer.duplicate());
    }
}
