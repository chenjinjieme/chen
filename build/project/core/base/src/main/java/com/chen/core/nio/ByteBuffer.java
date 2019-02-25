package com.chen.core.nio;

import com.chen.core.lang.ByteSequence;

public class ByteBuffer {
    private byte[] array;
    private int position;
    private int limit;
    private int capacity;

    private ByteBuffer(byte[] array, int position, int limit, int capacity) {
        this.array = array;
        this.position = position;
        this.limit = limit;
        this.capacity = capacity;
    }

    public byte[] array() {
        return array;
    }

    public int position() {
        return position;
    }

    public ByteBuffer position(int position) {
        this.position = position;
        return this;
    }

    public int limit() {
        return limit;
    }

    public ByteBuffer limit(int limit) {
        this.limit = limit;
        return this;
    }

    public int capacity() {
        return capacity;
    }

    public static ByteBuffer allocate(int capacity) {
        return new ByteBuffer(new byte[capacity], 0, capacity, capacity);
    }

    public static ByteBuffer wrap(byte[] bytes) {
        return new ByteBuffer(bytes, 0, bytes.length, bytes.length);
    }

    public byte get() {
        return array[position++];
    }

    public ByteBuffer put(byte b) {
        array[position++] = b;
        return this;
    }

    public byte get(int index) {
        return array[index];
    }

    public ByteBuffer put(int index, byte b) {
        array[index] = b;
        return this;
    }

    public ByteBuffer get(byte[] bytes) {
        return get(bytes, 0, bytes.length);
    }

    public ByteBuffer get(byte[] bytes, int offset, int length) {
        System.arraycopy(array, position, bytes, offset, length);
        position += length;
        return this;
    }

    public ByteBuffer put(byte[] bytes) {
        return put(bytes, 0, bytes.length);
    }

    public ByteBuffer put(byte[] bytes, int offset, int length) {
        System.arraycopy(bytes, offset, array, position, length);
        position += length;
        return this;
    }

    public ByteSequence toByteSequence(int offset, int length) {
        return new ByteSequence(array, offset, length);
    }
}
