package com.chen.core.bencode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.NonWritableChannelException;
import java.util.Iterator;

public class MultiByteChannel implements ByteChannel {
    private final Iterator<? extends ByteChannel> iterator;
    private ByteChannel next;

    public MultiByteChannel(Iterator<? extends ByteChannel> iterator) {
        this.iterator = iterator;
        next = iterator.hasNext() ? iterator.next() : null;
    }

    public int read(ByteBuffer dst) throws IOException {
        if (next == null) return -1;
        var read = 0;
        for (; dst.hasRemaining(); ) {
            var i = next.read(dst);
            if (i >= 0) read += i;
            else if (iterator.hasNext()) next = iterator.next();
            else {
                next = null;
                return read;
            }
        }
        return read;
    }

    public int write(ByteBuffer src) {
        throw new NonWritableChannelException();
    }

    public boolean isOpen() {
        return next != null;
    }

    public void close() {
        next = null;
    }
}
