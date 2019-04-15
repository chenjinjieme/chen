package com.chen.core.nio;

import java.nio.ByteBuffer;

public class ByteBuffers {
    public static void put(ByteBuffer dst, ByteBuffer src) {
        for (int i = 0, n = Math.min(src.remaining(), dst.remaining()); i < n; i++) dst.put(src.get());
    }

    public static void put(ByteBuffer dst, ByteBuffer src, int length) {
        for (int i = 0, n = Math.min(Math.min(src.remaining(), dst.remaining()), length); i < n; i++) dst.put(src.get());
    }
}
