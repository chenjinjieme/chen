package com.chen.core.io;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends ByteArrayOutputStream {
    public ByteBufferOutputStream put(byte[] bytes) {
        return put(bytes, 0, bytes.length);
    }

    public ByteBufferOutputStream put(byte[] bytes, int offset, int length) {
        write(bytes, offset, length);
        return this;
    }

    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }
}
