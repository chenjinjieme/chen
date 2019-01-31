package com.chen.proxy.nio.channels;

import sun.nio.ch.IOUtil;
import sun.nio.ch.SelChImpl;
import sun.nio.ch.SelectionKeyImpl;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;

abstract class WrappedSocketChannel extends AbstractSelectableChannel implements SelChImpl {
    SocketChannel socket;

    WrappedSocketChannel(SocketChannel socket) throws IOException {
        super(socket.provider());
        (this.socket = socket).configureBlocking(false);
        configureBlocking(false);
    }

    public FileDescriptor getFD() {
        return ((SelChImpl) socket).getFD();
    }

    public int getFDVal() {
        return ((SelChImpl) socket).getFDVal();
    }

    public boolean translateAndUpdateReadyOps(int i, SelectionKeyImpl selectionKey) {
        return ((SelChImpl) socket).translateAndUpdateReadyOps(i, selectionKey);
    }

    public boolean translateAndSetReadyOps(int i, SelectionKeyImpl selectionKey) {
        return ((SelChImpl) socket).translateAndSetReadyOps(i, selectionKey);
    }

    public int translateInterestOps(int i) {
        return ((SelChImpl) socket).translateInterestOps(i);
    }

    public int validOps() {
        return socket.validOps();
    }

    public void kill() throws IOException {
        ((SelChImpl) socket).kill();
    }

    protected void implCloseSelectableChannel() throws IOException {
        socket.close();
    }

    protected void implConfigureBlocking(boolean block) throws IOException {
        IOUtil.configureBlocking(getFD(), block);
    }
}
