package com.chen.core.bencode;

import com.chen.core.base.lang.ByteSequence;
import com.chen.core.base.nio.ByteBuffer;

public class ByteString implements Value {
    private ByteSequence sequence;

    public ByteString(ByteSequence sequence) {
        this.sequence = sequence;
    }

    public ByteString(String s) {
        sequence = new ByteSequence(s.getBytes());
    }

    private ByteString(ByteBuffer buffer) {
        int length = 0;
        for (int b = buffer.get(); b != ':'; b = buffer.get()) length = length * 10 + b - '0';
        sequence = buffer.toByteSequence(buffer.position(), length);
        buffer.position(buffer.position() + length);
    }

    public int length() {
        int length = sequence.length();
        return java.lang.Integer.toString(length).length() + length + 1;
    }

    public static ByteString parse(ByteBuffer buffer) {
        return new ByteString(buffer);
    }

    public void write(ByteBuffer buffer) {
        buffer.put(java.lang.Integer.toString(sequence.length()).getBytes()).put((byte) ':').put(sequence.bytes(), sequence.offset(), sequence.length());
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ByteString) return ((ByteString) obj).sequence.equals(sequence);
        return false;
    }

    public int hashCode() {
        return sequence.hashCode();
    }

    public String toString() {
        return sequence.toString();
    }
}
