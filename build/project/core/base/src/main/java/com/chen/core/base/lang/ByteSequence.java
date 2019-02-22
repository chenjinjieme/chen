package com.chen.core.base.lang;

import java.util.Arrays;

public class ByteSequence {
    private final byte[] bytes;
    private final int offset;
    private final int length;

    public ByteSequence(byte[] bytes) {
        this.bytes = bytes;
        offset = 0;
        length = bytes.length;
    }

    public ByteSequence(byte[] bytes, int offset) {
        if (offset < 0 || offset >= bytes.length) throw new StringIndexOutOfBoundsException();
        this.bytes = bytes;
        this.offset = offset;
        length = bytes.length - offset;
    }

    public ByteSequence(byte[] bytes, int offset, int length) {
        if (offset < 0 || length < 0 || offset >= bytes.length || offset + length > bytes.length)
            throw new StringIndexOutOfBoundsException();
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    public byte[] bytes() {
        return bytes;
    }

    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }

    private int ix(int i) {
        return i + offset;
    }

    public byte get(int i) {
        return bytes[ix(i)];
    }

    public ByteSequence put(int i, byte x) {
        bytes[i = ix(i)] = x;
        return this;
    }

    public short getShort(int i) {
        return (short) (bytes[i = ix(i)] << 8 | bytes[++i] & 0xff);
    }

    public ByteSequence putShort(int i, short x) {
        bytes[i = ix(i)] = (byte) (x >> 8);
        bytes[++i] = (byte) x;
        return this;
    }

    public int getInt(int i) {
        return bytes[i = ix(i)] << 24 | (bytes[++i] & 0xff) << 16 | (bytes[++i] & 0xff) << 8 | bytes[++i] & 0xff;
    }

    public ByteSequence putInt(int i, int x) {
        bytes[i = ix(i)] = (byte) (x >> 24);
        bytes[++i] = (byte) (x >> 16);
        bytes[++i] = (byte) (x >> 8);
        bytes[++i] = (byte) x;
        return this;
    }

    public long getLong(int i) {
        return ((long) bytes[i = ix(i)]) << 56 | ((long) bytes[++i] & 0xff) << 48 | ((long) bytes[++i] & 0xff) << 40 | ((long) bytes[++i] & 0xff) << 32 | ((long) bytes[++i] & 0xff) << 24 | (bytes[++i] & 0xff) << 16 | (bytes[++i] & 0xff) << 8 | bytes[++i] & 0xff;
    }

    public ByteSequence putLong(int i, long x) {
        bytes[i = ix(i)] = (byte) (x >> 56);
        bytes[++i] = (byte) (x >> 48);
        bytes[++i] = (byte) (x >> 40);
        bytes[++i] = (byte) (x >> 32);
        bytes[++i] = (byte) (x >> 24);
        bytes[++i] = (byte) (x >> 16);
        bytes[++i] = (byte) (x >> 8);
        bytes[++i] = (byte) x;
        return this;
    }

    public int indexOf(Byte b) {
        return indexOf(b, 0);
    }

    public int indexOf(Byte b, int x) {
        for (int i = ix(x), l = ix(length); i < l; i++) if (bytes[i] == b) return i - offset;
        return -1;
    }

    public int indexOf(ByteSequence sequence) {
        return indexOf(sequence, 0);
    }

    public int indexOf(ByteSequence sequence, int x) {
        if (sequence.length == 0) return x;
        byte b = sequence.bytes[sequence.offset];
        for (int i = ix(x), l = ix(length - sequence.length); i <= l; i++)
            if (bytes[i] == b) {
                int j = i + 1, k = sequence.ix(1), n = i + sequence.length;
                for (; j < n && bytes[j] == sequence.bytes[k]; j++) k++;
                if (j == n) return i - offset;
            }
        return -1;
    }

    public ByteSequence subSequence(int start) {
        return start == 0 ? this : new ByteSequence(bytes, ix(start));
    }

    public ByteSequence subSequence(int start, int end) {
        return start == 0 && end == length ? this : new ByteSequence(bytes, ix(start), end - start);
    }

    public int hashCode() {
        var hashCode = 0;
        for (int i = offset, l = offset + length; i < l; i++) hashCode = 31 * hashCode + (bytes[i] & 0xff);
        return hashCode;
    }

    public boolean equals(Object obj) {
        return obj instanceof ByteSequence && Arrays.equals(((ByteSequence) obj).bytes, ((ByteSequence) obj).offset, ((ByteSequence) obj).offset + ((ByteSequence) obj).length, bytes, offset, offset + length);
    }

    public String toString() {
        return new String(bytes, offset, length);
    }
}
