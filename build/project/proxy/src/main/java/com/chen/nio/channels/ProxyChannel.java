package com.chen.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

public class ProxyChannel extends WrappedSocketChannel implements ByteChannel {
    private ByteChannel channel;
    private boolean connect;
    private boolean work;

    public ProxyChannel(SocketChannel socket) throws IOException {
        this(socket, socket);
    }

    ProxyChannel(SocketChannel socket, ByteChannel channel) throws IOException {
        super(socket);
        this.channel = channel;
    }

    public boolean getConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public boolean getWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    public int write(ByteBuffer src) throws IOException {
        int write = 0;
        for (int i; src.remaining() > 0; write += i) if ((i = channel.write(src)) < 0) return i;
        return write;
    }
}
