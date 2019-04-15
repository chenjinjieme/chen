package com.chen.core.io;

import com.chen.core.lang.ByteSequence;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class ByteBufferOutputStream extends ByteArrayOutputStream implements Iterable<Byte> {
    public ByteBufferOutputStream put(byte b) {
        write(b);
        return this;
    }

    public ByteBufferOutputStream put(byte[] bytes) {
        return put(bytes, 0, bytes.length);
    }

    public ByteBufferOutputStream put(byte[] bytes, int offset, int length) {
        write(bytes, offset, length);
        return this;
    }

    public ByteBufferOutputStream clear() {
        reset();
        return this;
    }

    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }

    public ByteSequence toByteSequence() {
        return new ByteSequence(buf, 0, count);
    }

    public Iterator<Byte> iterator() {
        return new ByteIterator();
    }

    public class ByteIterator implements Iterator<Byte> {
        private int index;

        public boolean hasNext() {
            return index < count;
        }

        public Byte next() {
            return buf[index++];
        }
    }
}
