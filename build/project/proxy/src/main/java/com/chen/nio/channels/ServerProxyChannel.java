package com.chen.nio.channels;

import com.chen.util.crypto.AES;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ServerProxyChannel extends ProxyChannel {
    public ServerProxyChannel(SocketChannel socket, AES aes) throws IOException {
        super(socket, new AESChannel(socket, aes));
    }
}
