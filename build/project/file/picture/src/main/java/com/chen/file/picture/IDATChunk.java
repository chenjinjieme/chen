package com.chen.file.picture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

class IDATChunk implements Chunk {
    byte[] data;
    List<Integer> lengths;

    private IDATChunk(byte[] data, List<Integer> lengths) {
        this.data = data;
        this.lengths = lengths;
    }

    static Chunk parse(ByteBuffer buffer) {
        var lengths = new ArrayList<Integer>();
        var l = 0;
        var position = buffer.position();
        for (int i = position, type = IDAT, length; type == IDAT; i += length + 12, type = buffer.getInt(i + 4)) {
            l += length = buffer.getInt(i);
            lengths.add(length);
        }
        var data = new byte[l];
        l = 0;
        position += 8;
        for (var length : lengths) {
            System.arraycopy(buffer.array(), position, data, l, length);
            position += length + 12;
            l += length;
        }
        buffer.position(position - 8);
        return new IDATChunk(data, lengths);
    }

    public void write(ByteChannel channel) throws IOException {
        var crc32 = new CRC32();
        var i = 0;
        var buffer = ByteBuffer.allocate(8).position(4).putInt(IDAT);
        for (var length : lengths) {
            crc32.reset();
            crc32.update(buffer.array(), 4, 4);
            crc32.update(data, i, length);
            channel.write(buffer.clear().putInt(length).clear());
            channel.write(ByteBuffer.wrap(data, i, length));
            channel.write(buffer.clear().putInt((int) crc32.getValue()).flip());
            i += length;
        }
    }
}
