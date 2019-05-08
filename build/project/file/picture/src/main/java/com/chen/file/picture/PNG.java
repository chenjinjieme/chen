package com.chen.file.picture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PNG {
    private static final long SIGNATURE = 0x89504E470D0A1A0AL;
    private List<Chunk> chunks;
    private IHDRChunk ihdr;
    private IDATChunk idat;
    private IENDChunk iend;

    private PNG(List<Chunk> chunks, IDATChunk idat) {
        this.chunks = chunks;
        ihdr = (IHDRChunk) chunks.get(0);
        this.idat = idat;
        iend = (IENDChunk) chunks.get(chunks.size() - 1);
    }

    public static PNG parse(Path path) throws IOException {
        try (var channel = FileChannel.open(path)) {
            var buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);
            buffer.flip().getLong();
            var chunks = new ArrayList<Chunk>();
            var idat = (IDATChunk) null;
            for (; buffer.hasRemaining(); ) {
                var chunk = Chunk.parse(buffer);
                if (chunk instanceof IDATChunk) idat = (IDATChunk) chunk;
                chunks.add(chunk);
            }
            return new PNG(chunks, idat);
        }
    }

    public int width() {
        return ihdr.width;
    }

    public int height() {
        return ihdr.height;
    }

    public byte bitDepth() {
        return ihdr.bitDepth;
    }

    public byte colorType() {
        return ihdr.colorType;
    }

    public ByteBuffer data() throws DataFormatException {
        var inflater = new Inflater();
        var buffer = ByteBuffer.allocate(idat.data.length << 2);
        inflater.setInput(idat.data);
        inflater.inflate(buffer);
        for (; !inflater.needsInput(); ) {
            buffer = ByteBuffer.allocate(buffer.capacity() << 1).put(buffer.flip());
            inflater.inflate(buffer);
        }
        return buffer.flip();
    }

    public PNG redeflater() throws DataFormatException {
        var deflater = new Deflater();
        var data = data();
        var buffer = ByteBuffer.allocate(data.limit());
        deflater.setInput(data);
        deflater.deflate(buffer);
        var length = buffer.position();
        var bytes = new byte[length];
        System.arraycopy(buffer.array(), 0, bytes, 0, length);
        idat.data = bytes;
        idat.lengths = List.of(length);
        return this;
    }

    public PNG setIDATSize(int size) {
        if (size == -1) idat.lengths = List.of(idat.data.length);
        else {
            var list = new ArrayList<Integer>();
            var i = idat.data.length;
            for (; i > size; i -= size) list.add(size);
            if (i > 0) list.add(i);
            idat.lengths = list;
        }
        return this;
    }

    public void write(Path path) throws IOException {
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            channel.write(ByteBuffer.allocate(8).putLong(SIGNATURE).flip());
            ihdr.write(channel);
            idat.write(channel);
            iend.write(channel);
        }
    }

    public void writeAll(Path path) throws IOException {
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            channel.write(ByteBuffer.allocate(8).putLong(SIGNATURE).flip());
            for (var chunk : chunks) chunk.write(channel);
        }
    }
}
