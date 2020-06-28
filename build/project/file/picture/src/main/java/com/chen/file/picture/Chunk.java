package com.chen.file.picture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

interface Chunk {
    int IHDR = 0x49484452;
    int IDAT = 0x49444154;
    int IEND = 0x49454e44;

    static Chunk parse(ByteBuffer buffer) {
        switch (buffer.getInt(buffer.position() + 4)) {
            case IHDR:
                return IHDRChunk.parse(buffer);
            case IDAT:
                return IDATChunk.parse(buffer);
            case IEND:
                return IENDChunk.parse(buffer);
            default:
                return RawChunk.parse(buffer);
        }
    }

    void write(ByteChannel channel) throws IOException;
}
