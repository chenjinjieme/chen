package com.chen.api.httpClient;

import java.nio.ByteBuffer;

public class ByteBuffers {
    public static int put(ByteBuffer dst, ByteBuffer src) {
        var n = Math.min(src.remaining(), dst.remaining());
        for (var i = 0; i < n; i++) dst.put(src.get());
        return n;
    }

    public static int put(ByteBuffer dst, ByteBuffer src, int length) {
        var n = Math.min(Math.min(src.remaining(), dst.remaining()), length);
        for (var i = 0; i < n; i++) dst.put(src.get());
        return n;
    }
}
