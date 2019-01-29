package com.chen.net.proxy;

import com.chen.nio.channels.RequestChannel;

public class ParentProxy extends Proxy {
    private String proxyHost;
    private int proxyPort;

    public ParentProxy(int port, String proxyHost, int proxyPort) {
        super(port);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    RequestChannel setRequest(RequestChannel requestChannel) {
        return requestChannel.setSend(true).setHost(proxyHost).setPort(proxyPort);
    }
}
