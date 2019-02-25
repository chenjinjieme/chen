package com.chen.core.bencode;

import com.chen.core.nio.ByteBuffer;

public interface Value {
    static Value parse(ByteBuffer buffer) {
        switch (buffer.get(buffer.position())) {
            case 'i':
                return Integer.parse(buffer);
            case 'l':
                return List.parse(buffer);
            case 'd':
                return Dictionary.parse(buffer);
            default:
                return ByteString.parse(buffer);
        }
    }

    int length();

    void write(ByteBuffer buffer);
}
