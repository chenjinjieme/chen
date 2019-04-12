package com.chen.core.net.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static javax.net.ssl.SSLEngineResult.HandshakeStatus.*;
import static javax.net.ssl.SSLEngineResult.Status.OK;

public class Engine {
    private SSLEngine engine;
    private ByteBuffer net = ByteBuffer.allocateDirect(16921);
    private ByteBuffer app = ByteBuffer.allocateDirect(16916);

    public Engine(SSLContext context, boolean b) {
        (engine = context.createSSLEngine()).setUseClientMode(b);
    }

    public Engine beginHandshake(SocketChannel channel) throws IOException {
        engine.beginHandshake();
        for (HandshakeStatus handshakeStatus = engine.getHandshakeStatus(); handshakeStatus != NOT_HANDSHAKING; )
            if (handshakeStatus == NEED_WRAP) {
                engine.wrap(app, net.clear());
                channel.write(net.flip());
                handshakeStatus = engine.getHandshakeStatus();
            } else {
                net.position(0).limit(0);
                for (; handshakeStatus == NEED_UNWRAP; ) {
                    channel.read(net.mark().position(net.limit()).limit(net.capacity()));
                    net.limit(net.position()).reset();
                    for (; handshakeStatus == NEED_UNWRAP && engine.unwrap(net, app).getStatus() == OK; )
                        if ((handshakeStatus = engine.getHandshakeStatus()) == NEED_TASK) {
                            engine.getDelegatedTask().run();
                            handshakeStatus = engine.getHandshakeStatus();
                        }
                }
            }
        return this;
    }

    public ByteBuffer wrap(ByteBuffer buffer) throws SSLException {
        engine.wrap(buffer, net.clear());
        return net.flip();
    }

    public ByteBuffer unwrap(ByteBuffer buffer) throws SSLException {
        engine.unwrap(buffer, app.clear());
        return app.flip();
    }
}
