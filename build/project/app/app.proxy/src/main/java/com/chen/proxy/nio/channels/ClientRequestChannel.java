package com.chen.proxy.nio.channels;

import com.chen.proxy.crypto.AES;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientRequestChannel extends RequestChannel {
    public ClientRequestChannel(String proxyHost, int proxyPort, AES aes) throws IOException {
        this(SocketChannel.open(), proxyHost, proxyPort, aes);
    }

    private ClientRequestChannel(SocketChannel socket, String proxyHost, int proxyPort, AES aes) throws IOException {
        super(socket, new AESChannel(socket, aes));
        socket.connect(new InetSocketAddress(proxyHost, proxyPort));
        for (; !socket.finishConnect(); ) ;
    }
}
