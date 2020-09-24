package com.chen.file.image.png;

import com.chen.core.nio.channels.ByteBufferChannel;
import com.chen.core.nio.channels.MultiByteChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PNG {
    private static final ByteBuffer SIGNATURE = ByteBuffer.wrap(new byte[]{0xffffff89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a});
    private final List<Chunk> chunks;
    private final IHDRChunk ihdr;
    private final IDATChunk idat;
    private final IENDChunk iend;

    private PNG(List<Chunk> chunks, IHDRChunk ihdr, IDATChunk idat, IENDChunk iend) {
        this.chunks = chunks;
        this.ihdr = ihdr;
        this.idat = idat;
        this.iend = iend;
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

    public static PNG parse(Path path) throws IOException {
        try (var channel = FileChannel.open(path)) {
            var buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);
            buffer.position(8);
            var chunks = new ArrayList<Chunk>();
            var ihdr = IHDRChunk.parse(buffer);
            chunks.add(ihdr);
            var idat = (IDATChunk) null;
            for (; buffer.hasRemaining(); ) {
                var chunk = Chunk.parse(buffer);
                chunks.add(chunk);
                if (chunk instanceof IDATChunk) {
                    idat = (IDATChunk) chunk;
                    break;
                }
            }
            var iend = (IENDChunk) null;
            for (; buffer.hasRemaining(); ) {
                var chunk = Chunk.parse(buffer);
                chunks.add(chunk);
                if (chunk instanceof IENDChunk) {
                    iend = (IENDChunk) chunk;
                    break;
                }
            }
            return new PNG(chunks, ihdr, idat, iend);
        }
    }

    public ByteBuffer data() throws DataFormatException {
        var inflater = new Inflater();
        var data = idat.data;
        var buffer = ByteBuffer.allocate(data.length << 2);
        inflater.setInput(data);
        inflater.inflate(buffer);
        for (; !inflater.needsInput(); ) {
            buffer = ByteBuffer.allocate(buffer.capacity() << 1).put(buffer.flip());
            inflater.inflate(buffer);
        }
        return buffer.flip().slice();
    }

    public PNG deflate() throws DataFormatException {
        var deflater = new Deflater();
        var data = data();
        var buffer = ByteBuffer.allocate(data.remaining());
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
        var length = idat.data.length;
        if (size > 0 && size < length) {
            var list = new ArrayList<Integer>();
            for (; length > size; length -= size) list.add(size);
            if (length > 0) list.add(length);
            idat.lengths = list;
        } else idat.lengths = List.of(length);
        return this;
    }

    public Image image() throws DataFormatException {
        return new Image(ihdr, data());
    }

    public ByteChannel channel() {
        return new MultiByteChannel(List.of(new ByteBufferChannel(SIGNATURE.duplicate()), ihdr.channel(), idat.channel(), iend.channel()).iterator());
    }

    public ByteChannel allChannel() {
        return new MultiByteChannel(List.of(new ByteBufferChannel(SIGNATURE.duplicate()), new MultiByteChannel(chunks.stream().map(Chunk::channel).iterator())).iterator());
    }

    private void write(Path path, ByteChannel src) throws IOException {
        try (var channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            var buffer = ByteBuffer.allocate(8192);
            for (var read = src.read(buffer); read > 0; read = src.read(buffer.clear())) channel.write(buffer.flip());
        }
    }

    public void write(Path path) throws IOException {
        write(path, channel());
    }

    public void writeAll(Path path) throws IOException {
        write(path, allChannel());
    }
}
