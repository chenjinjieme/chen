package com.chen.core.nio.channels;

import com.chen.core.net.ssl.Engine;
import com.chen.core.nio.ByteBuffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class SSLChannel extends WrappedByteChannel {
    private Engine engine;
    private ByteBuffer net = ByteBuffer.allocate(16921);
    private ByteBuffer app = ByteBuffer.allocate(16916);

    public SSLChannel(ByteChannel channel, Engine engine) {
        super(channel);
        this.engine = engine;
    }

    public int read(ByteBuffer dst) throws IOException {
        var position = dst.position();
        if (app.position() > 0) {
            ByteBuffers.put(dst, app.flip());
            app.compact();
        } else if (super.read(net) > 0 || net.position() > 0) {
            var unwrap = engine.unwrap(net.flip());
            net.compact();
            ByteBuffers.put(dst, unwrap);
            if (unwrap.hasRemaining()) app.put(unwrap);
        }
        return dst.position() - position;
    }

    public int write(ByteBuffer src) throws IOException {
        var write = src.remaining();
        for (; src.hasRemaining(); ) super.write(engine.wrap(src));
        return write;
    }
}
