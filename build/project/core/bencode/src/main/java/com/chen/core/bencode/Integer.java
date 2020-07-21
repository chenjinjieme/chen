package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class Integer implements Bencode {
    private final long value;
    private ByteBuffer buffer;
    private String string;
    private int hashCode;

    private Integer(long value, ByteBuffer buffer) {
        this.value = value;
        this.buffer = buffer;
    }

    public Integer(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    public static Integer parse(ByteBuffer buffer) {
        var duplicate = buffer.duplicate();
        long value = buffer.position(buffer.position() + 1).get() - '0';
        for (var b = buffer.get(); b != 'e'; b = buffer.get()) value = value * 10 + b - '0';
        return new Integer(value, duplicate.limit(buffer.position()));
    }

    private ByteBuffer buffer() {
        byte[] bytes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 'e'};
        var x = value;
        var i = 19;
        for (; x > 9; i--, x /= 10) bytes[i] = (byte) (x % 10 + '0');
        bytes[i--] = (byte) (x + '0');
        bytes[i] = 'i';
        return ByteBuffer.wrap(bytes).position(i);
    }

    public ByteChannel channel() {
        return new ByteBufferChannel((buffer == null ? buffer = buffer() : buffer).duplicate());
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof Integer && ((Integer) obj).value == value;
    }

    public int hashCode() {
        return hashCode == 0 ? hashCode = Long.hashCode(value) : hashCode;
    }

    public String toString() {
        return string == null ? string = Long.toString(value) : string;
    }
}
