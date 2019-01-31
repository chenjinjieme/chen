package com.chen.proxy.nio.channels;

import com.chen.base.crypto.AES;
import com.chen.base.nio.channels.AESChannel;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ServerProxyChannel extends ProxyChannel {
    public ServerProxyChannel(SocketChannel socket, AES aes) throws IOException {
        super(socket, new AESChannel(socket, aes));
    }
}
