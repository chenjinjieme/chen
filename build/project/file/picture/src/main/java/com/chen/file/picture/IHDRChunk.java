package com.chen.file.picture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.zip.CRC32;

class IHDRChunk implements Chunk {
    int width;
    int height;
    byte bitDepth;
    byte colorType;
    private byte compressionMethod;
    private byte filterMethod;
    private byte interlaceMethod;

    private IHDRChunk(int width, int height, byte bitDepth, byte colorType, byte compressionMethod, byte filterMethod, byte interlaceMethod) {
        this.width = width;
        this.height = height;
        this.bitDepth = bitDepth;
        this.colorType = colorType;
        this.compressionMethod = compressionMethod;
        this.filterMethod = filterMethod;
        this.interlaceMethod = interlaceMethod;
    }

    static Chunk parse(ByteBuffer buffer) {
        var chunk = new IHDRChunk(buffer.position(buffer.position() + 8).getInt(), buffer.getInt(), buffer.get(), buffer.get(), buffer.get(), buffer.get(), buffer.get());
        buffer.position(buffer.position() + 4);
        return chunk;
    }

    public void write(ByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(25).putInt(13).putInt(IHDR).putInt(width).putInt(height).put(bitDepth).put(colorType).put(compressionMethod).put(filterMethod).put(interlaceMethod);
        var crc32 = new CRC32();
        crc32.update(buffer.flip().position(4));
        channel.write(buffer.limit(25).putInt((int) crc32.getValue()).flip());
    }
}
