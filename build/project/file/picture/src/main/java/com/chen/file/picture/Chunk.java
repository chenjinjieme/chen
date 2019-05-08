package com.chen.file.picture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

interface Chunk {
    int IHDR = 0x49484452;
    int IDAT = 0x49444154;
    int IEND = 0x49454e44;

    static Chunk parse(ByteBuffer buffer) {
        var type = buffer.getInt(buffer.position() + 4);
        return type == IHDR ? IHDRChunk.parse(buffer) : type == IDAT ? IDATChunk.parse(buffer) : type == IEND ? IENDChunk.parse(buffer) : RawChunk.parse(buffer);
    }

    void write(ByteChannel channel) throws IOException;
}
