package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.util.stream.LongStream;

public class Integer implements Value {
    private long value;

    public Integer(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    public static Integer parse(ByteBuffer buffer) {
        return new Integer(LongStream.iterate(buffer.position(buffer.position() + 1).get(), b -> b != 'e', b -> buffer.get()).reduce(0L, (value, b) -> value * 10 + b - '0'));
    }

    public int bufferSize() {
        return Values.length(value) + 2;
    }

    public void write(ByteBuffer buffer) {
        buffer.put((byte) 'i').put(Long.toString(value).getBytes()).put((byte) 'e');
    }

    public boolean equals(Object obj) {
        return obj instanceof Integer && ((Integer) obj).value == value;
    }

    public int hashCode() {
        return Long.hashCode(value);
    }

    public String toString() {
        return Long.toString(value);
    }
}
