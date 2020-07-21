package com.chen.proxy.main;

import com.chen.proxy.nio.channels.ProxyChannel;
import com.chen.proxy.nio.channels.RequestChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;

public class Proxy {
    private static final byte[] SSL = "HTTP/1.1 200 Connection established\r\n\r\n".getBytes();
    private final Object wakeup = new Object();
    private int port;
    private Selector selector;
    private ExecutorService pool = Executors.newCachedThreadPool();

    public Proxy(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(port));
        selector = Selector.open();
        pool.submit(() -> {
            for (; ; ) {
                SocketChannel socket = serverSocketChannel.accept();
                pool.submit(() -> {
                    register(newProxyChannel(socket), newRequestChannel());
                    return null;
                });
            }
        });
        pool.submit(() -> {
            for (; ; ) {
                if (selector.select() > 0)
                    for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); iterator.remove()) {
                        SelectionKey key = iterator.next();
                        if (key.isValid()) handle(key);
                    }
                synchronized (wakeup) {
                    wakeup.wait(1);
                }
            }
        });
    }

    private void handle(SelectionKey key) {
        ProxyChannel proxy = (ProxyChannel) key.channel();
        if (!proxy.getWork()) {
            proxy.setWork(true);
            pool.submit(() -> {
                ProxyChannel remote = (ProxyChannel) key.attachment();
                try {
                    if (remote.getConnect()) handleConnect(proxy, remote);
                    else if (proxy.getConnect()) handleSend(proxy, remote);
                    else handleRead(proxy, remote);
                } catch (IOException e) {
                    proxy.close();
                    remote.close();
                }
                return null;
            });
        }
    }

    private void handleConnect(ProxyChannel proxy, ProxyChannel remote) throws IOException {
        RequestChannel request = (RequestChannel) remote;
        int read = request.set(proxy);
        if (read > 0 && request.getRequest().check()) {
            setRequest(request).connect();
            register(request, OP_CONNECT, proxy);
        } else if (read == -1) {
            proxy.close();
            remote.close();
        } else proxy.setWork(false);
    }

    private void handleSend(ProxyChannel proxy, ProxyChannel remote) throws IOException {
        RequestChannel request = (RequestChannel) proxy;
        if (request.finishConnect()) {
            if (request.getSend()) request.send();
            else remote.write(ByteBuffer.wrap(SSL));
            register(request, OP_READ, remote);
            request.setConnect(false);
            proxy.setWork(false);
            remote.setWork(false);
        }
    }

    private void handleRead(ProxyChannel proxy, ProxyChannel remote) throws IOException {
        int read;
        for (ByteBuffer buffer = ByteBuffer.allocateDirect(8192); (read = proxy.read(buffer.clear())) > 0; )
            remote.write(buffer.flip());
        if (read == -1) {
            proxy.close();
            remote.close();
        }
        proxy.setWork(false);
    }

    ProxyChannel newProxyChannel(SocketChannel socket) throws IOException {
        return new ProxyChannel(socket);
    }

    RequestChannel newRequestChannel() throws IOException {
        return new RequestChannel();
    }

    void register(ProxyChannel proxy, RequestChannel request) throws ClosedChannelException {
        register(proxy, OP_READ, request);
    }

    void register(ProxyChannel proxy, int ops, Object att) throws ClosedChannelException {
        synchronized (wakeup) {
            selector.wakeup();
            proxy.register(selector, ops, att);
        }
    }

    RequestChannel setRequest(RequestChannel request) {
        return request.setAddress();
    }
}
