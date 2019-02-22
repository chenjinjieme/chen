package com.chen.core.bencode;

import com.chen.core.base.nio.ByteBuffer;

public class Integer implements Value {
    private long value;

    private Integer(ByteBuffer buffer) {
        if (buffer.get() != 'i') throw new ParseException("not an integer");
        for (int b = buffer.get(); b != 'e'; b = buffer.get()) value = value * 10 + b - '0';
    }

    public long value() {
        return value;
    }

    public int length() {
        return toString().length() + 2;
    }

    public static Integer parse(ByteBuffer buffer) {
        return new Integer(buffer);
    }

    public void write(ByteBuffer buffer) {
        buffer.put((byte) 'i').put(((Long) value).toString().getBytes()).put((byte) 'e');
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
