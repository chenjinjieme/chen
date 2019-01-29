package com.chen.net.proxy;

import com.chen.nio.channels.ClientRequestChannel;
import com.chen.nio.channels.ProxyChannel;
import com.chen.nio.channels.RequestChannel;
import com.chen.util.crypto.AES;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import static java.nio.channels.SelectionKey.OP_READ;

public class ProxyClient extends Proxy {
    private String proxyHost;
    private int proxyPort;
    private AES aes;

    public ProxyClient(int port, String proxyHost, int proxyPort, AES aes) {
        super(port);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.aes = aes;
    }

    RequestChannel newRequestChannel() throws IOException {
        return new ClientRequestChannel(proxyHost, proxyPort, aes);
    }

    void register(ProxyChannel proxy, RequestChannel request) throws ClosedChannelException {
        register(proxy, OP_READ, request);
        register(request, OP_READ, proxy);
    }
}
