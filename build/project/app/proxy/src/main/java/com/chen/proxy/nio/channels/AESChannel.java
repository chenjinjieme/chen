package com.chen.proxy.nio.channels;

import com.chen.proxy.crypto.AES;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class AESChannel extends WrappedByteChannel {
    private AES aes;
    private int flag;
    private ByteBuffer buffer;
    private int read;
    private int l;

    public AESChannel(ByteChannel channel, AES aes) {
        super(channel);
        this.aes = aes;
    }

    public int read(ByteBuffer dst) throws IOException {
        switch (flag) {
            case 0:
                buffer = ByteBuffer.allocateDirect(4);
                flag = 1;
            case 1:
                int read = super.read(buffer);
                if (read < 0) return read;
                if ((this.read += read) < 4) break;
                buffer = ByteBuffer.allocate((l = buffer.flip().getInt()));
                flag = 2;
                this.read = 0;
            case 2:
                read = super.read(buffer);
                if (read < 0) return read;
                if ((this.read += read) < l) break;
                byte[] decrypt = aes.decrypt(buffer.array());
                dst.put(decrypt);
                flag = 0;
                this.read = 0;
                return decrypt.length;
        }
        return 0;
    }

    public int write(ByteBuffer src) throws IOException {
        byte[] bytes = new byte[src.remaining()];
        src.get(bytes);
        bytes = aes.encrypt(bytes);
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        int l = bytes.length;
        buffer.putInt(l);
        for (buffer.flip(); buffer.remaining() > 0; ) super.write(buffer);
        for (buffer = ByteBuffer.wrap(bytes); buffer.remaining() > 0; ) super.write(buffer);
        return l + 4;
    }
}
