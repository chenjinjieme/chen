package com.chen.core.nio.channels;

import com.chen.core.net.ssl.Engine;
import com.chen.core.nio.ByteBuffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class SSLChannel extends WrappedByteChannel {
    private Engine engine;
    private ByteBuffer net = ByteBuffer.allocateDirect(16921);
    private ByteBuffer app = ByteBuffer.allocateDirect(16916);

    public SSLChannel(ByteChannel channel, Engine engine) {
        super(channel);
        this.engine = engine;
    }

    public int read(ByteBuffer dst) throws IOException {
        var read = dst.position();
        if (app.position() > 0) {
            ByteBuffers.fill(dst, app.flip());
            app.compact();
        }
        for (; dst.hasRemaining() && (super.read(net) > 0 || net.position() > 0); ) {
            var unwrap = engine.unwrap(net.flip());
            net.compact();
            ByteBuffers.fill(dst, unwrap);
            if (unwrap.hasRemaining()) app.put(unwrap);
        }
        return dst.position() - read;
    }

    public int write(ByteBuffer src) throws IOException {
        var write = src.remaining();
        for (; src.hasRemaining(); ) super.write(engine.wrap(src));
        return write;
    }
}
