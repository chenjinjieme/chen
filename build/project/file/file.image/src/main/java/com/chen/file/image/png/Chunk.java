package com.chen.file.image.png;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

interface Chunk {
    int IHDR = 0x49484452;
    int IDAT = 0x49444154;
    int IEND = 0x49454e44;

    static Chunk parse(ByteBuffer buffer) {
        return switch (buffer.getInt(buffer.position() + 4)) {
            case IHDR -> IHDRChunk.parse(buffer);
            case IDAT -> IDATChunk.parse(buffer);
            case IEND -> IENDChunk.parse(buffer);
            default -> RawChunk.parse(buffer);
        };
    }

    ByteChannel channel();
}
