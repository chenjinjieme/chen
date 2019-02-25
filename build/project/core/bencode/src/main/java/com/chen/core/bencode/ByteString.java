package com.chen.core.bencode;

import com.chen.core.base.lang.ByteSequence;
import com.chen.core.base.nio.ByteBuffer;

public class ByteString implements Value {
    private ByteSequence sequence;

    public ByteString(ByteSequence sequence) {
        this.sequence = sequence;
    }

    public ByteString(String s) {
        this(new ByteSequence(s.getBytes()));
    }

    private ByteString(ByteBuffer buffer) {
        var length = 0;
        for (var b = buffer.get(); b != ':'; b = buffer.get()) length = length * 10 + b - '0';
        sequence = buffer.toByteSequence(buffer.position(), length);
        buffer.position(buffer.position() + length);
    }

    public int length() {
        var length = sequence.length();
        return java.lang.Integer.toString(length).length() + length + 1;
    }

    public static ByteString parse(ByteBuffer buffer) {
        return new ByteString(buffer);
    }

    public void write(ByteBuffer buffer) {
        buffer.put(java.lang.Integer.toString(sequence.length()).getBytes()).put((byte) ':').put(sequence.bytes(), sequence.offset(), sequence.length());
    }

    public int hashCode() {
        return sequence.hashCode();
    }

    public boolean equals(Object obj) {
        return obj == this || obj instanceof ByteString && ((ByteString) obj).sequence.equals(sequence);
    }

    public String toString() {
        return sequence.toString();
    }
}
