package com.chen.core.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.util.Iterator;

public class MultiByteChannel implements ByteChannel {
    private final Iterator<? extends ByteChannel> iterator;
    private ByteChannel next;
    private boolean close;

    public MultiByteChannel(Iterator<? extends ByteChannel> iterator) {
        this.iterator = iterator;
        next = iterator.hasNext() ? iterator.next() : null;
    }

    public int read(ByteBuffer dst) throws IOException {
        if (close) throw new ClosedChannelException();
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
        return !close;
    }

    public void close() {
        close = true;
    }
}
