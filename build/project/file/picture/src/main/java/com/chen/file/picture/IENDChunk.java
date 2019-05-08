package com.chen.file.picture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.zip.CRC32;

class IENDChunk implements Chunk {
    private static final IENDChunk INSTANCE = new IENDChunk();
    private static final ByteBuffer BUFFER = ByteBuffer.allocate(12);

    static {
        var crc32 = new CRC32();
        crc32.update(BUFFER.position(4).putInt(IEND).flip().position(4));
        BUFFER.limit(12).putInt((int) crc32.getValue()).flip();
    }

    static Chunk parse(ByteBuffer byteBuffer) {
        byteBuffer.position(byteBuffer.position() + 12);
        return INSTANCE;
    }

    public void write(ByteChannel channel) throws IOException {
        channel.write(BUFFER.duplicate());
    }
}
