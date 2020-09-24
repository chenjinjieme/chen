package com.chen.file.image.png;

import com.chen.core.nio.channels.ByteBufferChannel;
import com.chen.core.nio.channels.MultiByteChannel;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;

class IDATChunk implements Chunk {
    byte[] data;
    List<Integer> lengths;

    private IDATChunk(byte[] data, List<Integer> lengths) {
        this.data = data;
        this.lengths = lengths;
    }

    static IDATChunk parse(ByteBuffer buffer) {
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
        var array = buffer.array();
        for (var length : lengths) {
            System.arraycopy(array, position, data, l, length);
            position += length + 12;
            l += length;
        }
        buffer.position(position - 8);
        return new IDATChunk(data, lengths);
    }

    public ByteChannel channel() {
        var crc32 = new CRC32();
        var prefix = ByteBuffer.allocate(8).putInt(4, IDAT);
        var suffix = ByteBuffer.allocate(4);
        var prefixChannel = new ByteBufferChannel(prefix);
        var suffixChannel = new ByteBufferChannel(suffix);
        var array = prefix.array();
        var iterator = lengths.iterator();
        return new MultiByteChannel(new Iterator<>() {
            private int offset;

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public ByteChannel next() {
                var length = iterator.next();
                crc32.reset();
                crc32.update(array, 4, 4);
                crc32.update(data, offset, length);
                prefix.clear().putInt(0, length);
                var wrap = ByteBuffer.wrap(data, offset, length);
                suffix.clear().putInt(0, (int) crc32.getValue());
                offset += length;
                return new MultiByteChannel(List.of(prefixChannel, new ByteBufferChannel(wrap), suffixChannel).iterator());
            }
        });
    }
}
