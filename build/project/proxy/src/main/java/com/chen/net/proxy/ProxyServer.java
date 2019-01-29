package com.chen.net.proxy;

import com.chen.nio.channels.ProxyChannel;
import com.chen.nio.channels.ServerProxyChannel;
import com.chen.util.crypto.AES;

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
