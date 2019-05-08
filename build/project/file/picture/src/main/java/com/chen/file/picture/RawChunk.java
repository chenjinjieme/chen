package com.chen.file.picture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

class RawChunk implements Chunk {
    private ByteBuffer buffer;

    private RawChunk(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    static Chunk parse(ByteBuffer buffer) {
        var position = buffer.position();
        var length = buffer.getInt() + 12;
        return new RawChunk(ByteBuffer.wrap(buffer.position(position + length).array(), position, length));
    }

    public void write(ByteChannel channel) throws IOException {
        channel.write(buffer.duplicate());
    }
}
