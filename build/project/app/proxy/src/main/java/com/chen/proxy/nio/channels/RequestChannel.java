package com.chen.proxy.nio.channels;

import com.chen.core.net.URL;
import com.chen.core.net.http.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

public class RequestChannel extends ProxyChannel {
    private Request request;
    private boolean send;
    private String host;
    private int port;

    public RequestChannel() throws IOException {
        super(SocketChannel.open());
        setConnect(true);
        request = new Request();
    }

    RequestChannel(SocketChannel socket, ByteChannel channel) throws IOException {
        super(socket, channel);
    }

    public Request getRequest() {
        return request;
    }

    public boolean getSend() {
        return send;
    }

    public RequestChannel setSend(boolean send) {
        this.send = send;
        return this;
    }

    public RequestChannel setHost(String host) {
        this.host = host;
        return this;
    }

    public RequestChannel setPort(int port) {
        this.port = port;
        return this;
    }

    public int set(ProxyChannel proxy) throws IOException {
        return request.set(proxy);
    }

    public boolean connect() throws IOException {
        return socket.connect(new InetSocketAddress(host, port));
    }

    public boolean finishConnect() throws IOException {
        return socket.finishConnect();
    }

    public int send() throws IOException {
        return write(request.getBuffer());
    }

    public RequestChannel setAddress() {
        send = !"CONNECT".equals(request.getMethod());
        URL url = request.getURL();
        host = url.getHost();
        if (host == null) url.update(request.getHeaders().get("Host"));
        host = url.getHost();
        port = url.getPort();
        return this;
    }
}
