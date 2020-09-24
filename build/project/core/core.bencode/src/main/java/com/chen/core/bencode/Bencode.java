package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public interface Bencode {
    static Bencode parse(ByteBuffer buffer) {
        return switch (buffer.get(buffer.position())) {
            case 'i' -> Integer.parse(buffer);
            case 'l' -> List.parse(buffer);
            case 'd' -> Dictionary.parse(buffer);
            default -> ByteString.parse(buffer);
        };
    }

    ByteChannel channel();
}
