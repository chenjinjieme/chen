package com.chen.file.image.png;

import com.chen.core.nio.channels.ByteBufferChannel;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.zip.CRC32;

class IENDChunk implements Chunk {
    private static final ByteBuffer BUFFER = ByteBuffer.allocate(12);
    static final IENDChunk INSTANCE = new IENDChunk();

    static {
        var crc32 = new CRC32();
        crc32.update(BUFFER.putInt(4, IEND).array(), 4, 4);
        BUFFER.putInt(8, (int) crc32.getValue());
    }

    static IENDChunk parse(ByteBuffer byteBuffer) {
        byteBuffer.position(byteBuffer.position() + 12);
        return INSTANCE;
    }

    public ByteChannel channel() {
        return new ByteBufferChannel(BUFFER.duplicate());
    }
}
