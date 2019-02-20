package com.chen.proxy.main;

import com.chen.core.base.crypto.AES;
import com.chen.proxy.nio.channels.ProxyChannel;
import com.chen.proxy.nio.channels.ServerProxyChannel;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ProxyServer extends Proxy {
    private AES aes;

    public ProxyServer(int port, AES aes) {
        super(port);
        this.aes = aes;
    }

    ProxyChannel newProxyChannel(SocketChannel socket) throws IOException {
        return new ServerProxyChannel(socket, aes);
    }
}
