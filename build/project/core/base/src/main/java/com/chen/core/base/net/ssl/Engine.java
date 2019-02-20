package com.chen.core.base.net.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
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

    public SSLEngine beginHandshake(SocketChannel channel) throws IOException {
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
        return engine;
    }
}
