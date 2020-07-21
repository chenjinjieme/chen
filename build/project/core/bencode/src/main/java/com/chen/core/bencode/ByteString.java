package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.List;

public class ByteString implements Bencode {
    private final ByteBuffer buffer;
    private ByteBuffer prefix;
    private String string;
    private int hashCode;

    private ByteString(ByteBuffer buffer, ByteBuffer prefix) {
        this.buffer = buffer;
        this.prefix = prefix;
    }

    public ByteString(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteString(String string) {
        buffer = ByteBuffer.wrap(string.getBytes());
        this.string = string;
    }

    public ByteBuffer buffer() {
        return buffer;
    }

    public static ByteString parse(ByteBuffer buffer) {
        var prefix = buffer.duplicate();
        var length = buffer.get() - '0';
        for (var b = buffer.get(); b != ':'; b = buffer.get()) length = length * 10 + b - '0';
        var position = buffer.position();
        prefix.limit(position);
        var duplicate = buffer.duplicate().limit(length += position);
        buffer.position(length);
        return new ByteString(duplicate, prefix);
    }

    private ByteBuffer prefix() {
        byte[] bytes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, ':'};
        var x = buffer.remaining();
        var i = 9;
        for (; x > 9; i--, x /= 10) bytes[i] = (byte) (x % 10 + '0');
        bytes[i] = (byte) (x + '0');
        return ByteBuffer.wrap(bytes).position(i);
    }

    public ByteChannel channel() {
        return new MultiByteChannel(List.of(new ByteBufferChannel((prefix == null ? prefix = prefix() : prefix).duplicate()), new ByteBufferChannel(buffer.duplicate())).iterator());
    }

    public int hashCode() {
        return hashCode == 0 ? hashCode = buffer.hashCode() : hashCode;
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof ByteString && ((ByteString) obj).buffer.equals(buffer);
    }

    public String toString() {
        return string == null ? string = new String(buffer.array(), buffer.position(), buffer.remaining()) : string;
    }
}
