package com.chen.api.httpClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

public class HttpClient {
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final byte[] GET = "GET ".getBytes();
    private static final byte[] POST = "POST ".getBytes();
    private static final byte[] VERSION = " HTTP/1.1".getBytes();
    private static final byte[] HOST = "Host: ".getBytes();
    private static final byte[] CONTENT_LENGTH = "Content-Length: ".getBytes();

    public HttpResponseChannel get(URL url) throws IOException {
        return send(url, new ByteBufferOutputStream().put(GET).put(url.path().getBytes()).put(VERSION).put(CRLF).put(HOST).put(url.authority().getBytes()).put(CRLF).put(CRLF));
    }

    public HttpResponseChannel post(URL url) throws IOException {
        return send(url, new ByteBufferOutputStream().put(POST).put(url.path().getBytes()).put(VERSION).put(CRLF).put(HOST).put(url.authority().getBytes()).put(CRLF).put(CONTENT_LENGTH).put((byte) '0').put(CRLF).put(CRLF));
    }

    private HttpResponseChannel send(URL url, ByteBufferOutputStream stream) throws IOException {
        var socketChannel = SocketChannel.open(new InetSocketAddress(url.host(), url.port()));
        var channel = (ByteChannel) socketChannel.configureBlocking(false);
        if ("https".equals(url.protocol())) channel = new SSLChannel(socketChannel, new Engine(Context.getDefault(), true).beginHandshake(socketChannel));
        var buffer = stream.toByteBuffer();
        for (; buffer.hasRemaining(); ) channel.write(buffer);
        return new HttpResponseChannel(channel, stream);
    }
}
