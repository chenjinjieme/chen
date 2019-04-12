package com.chen.api.httpClient;

import com.chen.core.io.ByteBufferOutputStream;
import com.chen.core.net.URL;
import com.chen.core.net.ssl.Context;
import com.chen.core.net.ssl.Engine;
import com.chen.core.nio.channels.SSLChannel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

public class HttpClient {
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final byte[] GET = "GET ".getBytes();
    private static final byte[] BUFFER = " HTTP/1.1\r\nHost: ".getBytes();

    public ByteChannel get(URL url) throws IOException {
        var socketChannel = SocketChannel.open(new InetSocketAddress(url.host(), url.port()));
        var channel = (ByteChannel) socketChannel.configureBlocking(false);
        if ("https".equals(url.protocol())) channel = new SSLChannel(socketChannel, new Engine(Context.getDefault(), true).beginHandshake(socketChannel));
        var buffer = new ByteBufferOutputStream().put(GET).put(url.path().getBytes()).put(BUFFER).put(url.authority().getBytes()).put(CRLF).put(CRLF).toByteBuffer();
        for (; buffer.hasRemaining(); ) channel.write(buffer);
        return channel;
    }
}
