package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

public class ByteString implements Value {
    private ByteBuffer buffer;

    public ByteString(String s) {
        buffer = ByteBuffer.wrap(s.getBytes());
    }

    public ByteString(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer buffer() {
        return buffer;
    }

    public static ByteString parse(ByteBuffer buffer) {
        var length = IntStream.iterate(buffer.get(), b -> b != ':', b -> buffer.get()).reduce(0, (l, b) -> l * 10 + b - '0');
        var duplicate = buffer.duplicate().limit(length += buffer.position());
        buffer.position(length);
        return new ByteString(duplicate);
    }

    public int bufferSize() {
        var length = buffer.remaining();
        return Values.length(length) + length + 1;
    }

    public void write(ByteBuffer buffer) {
        buffer.put(java.lang.Integer.toString(this.buffer.remaining()).getBytes()).put((byte) ':').put(this.buffer.duplicate());
    }

    public int hashCode() {
        return buffer.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof ByteString && ((ByteString) obj).buffer.equals(buffer);
    }

    public String toString() {
        return new String(buffer.array(), buffer.position(), buffer.remaining());
    }
}
