package com.chen.nio.channels;

import java.io.IOException;
import java.nio.channels.Channel;

abstract class WrappedChannel<T extends Channel> implements Channel {
    T channel;

    WrappedChannel(T channel) {
        this.channel = channel;
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public void close() throws IOException {
        channel.close();
    }
}
