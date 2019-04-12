package com.chen.core.nio;

import java.nio.ByteBuffer;

public class ByteBuffers {
    public static void fill(ByteBuffer dst, ByteBuffer src) {
        var length = src.remaining();
        var remaining = dst.remaining();
        if (length > remaining) {
            var bytes = new byte[remaining];
            src.get(bytes);
            dst.put(bytes);
        } else dst.put(src);
    }
}
