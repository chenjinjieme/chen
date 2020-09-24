package com.chen.file.image.png;

import com.chen.core.nio.channels.ByteBufferChannel;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.zip.CRC32;

class IHDRChunk implements Chunk {
    final int width;
    final int height;
    final byte bitDepth;
    final byte colorType;
    final byte compressionMethod;
    final byte filterMethod;
    final byte interlaceMethod;
    private ByteBuffer buffer;

    private IHDRChunk(int width, int height, byte bitDepth, byte colorType, byte compressionMethod, byte filterMethod, byte interlaceMethod) {
        this.width = width;
        this.height = height;
        this.bitDepth = bitDepth;
        this.colorType = colorType;
        this.compressionMethod = compressionMethod;
        this.filterMethod = filterMethod;
        this.interlaceMethod = interlaceMethod;
    }

    static IHDRChunk parse(ByteBuffer buffer) {
        var chunk = new IHDRChunk(buffer.position(buffer.position() + 8).getInt(), buffer.getInt(), buffer.get(), buffer.get(), buffer.get(), buffer.get(), buffer.get());
        buffer.position(buffer.position() + 4);
        return chunk;
    }

    public ByteChannel channel() {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(25).putInt(13).putInt(IHDR).putInt(width).putInt(height).put(bitDepth).put(colorType).put(compressionMethod).put(filterMethod).put(interlaceMethod);
            var crc32 = new CRC32();
            crc32.update(buffer.flip().position(4));
            buffer.limit(25).putInt((int) crc32.getValue()).flip();
        }
        return new ByteBufferChannel(buffer.duplicate());
    }
}
