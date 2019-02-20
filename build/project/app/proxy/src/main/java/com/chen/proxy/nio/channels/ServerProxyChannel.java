package com.chen.proxy.nio.channels;

import com.chen.core.base.crypto.AES;
import com.chen.core.base.nio.channels.AESChannel;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ServerProxyChannel extends ProxyChannel {
    public ServerProxyChannel(SocketChannel socket, AES aes) throws IOException {
        super(socket, new AESChannel(socket, aes));
    }
}
