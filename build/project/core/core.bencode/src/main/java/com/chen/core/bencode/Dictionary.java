package com.chen.core.bencode;

import com.chen.core.nio.channels.ByteBufferChannel;
import com.chen.core.nio.channels.MultiByteChannel;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

public class Dictionary extends LinkedHashMap<ByteString, Bencode> implements Bencode {
    public static Dictionary parse(ByteBuffer buffer) {
        var dictionary = new Dictionary();
        for (var b = buffer.position(buffer.position() + 1).get(buffer.position()); b != 'e'; b = buffer.get(buffer.position())) dictionary.put(ByteString.parse(buffer), Bencode.parse(buffer));
        buffer.position(buffer.position() + 1);
        return dictionary;
    }

    public static Dictionary of(Bencode... bencodes) {
        var dictionary = new Dictionary();
        for (var i = 0; i < bencodes.length; i++) dictionary.put((ByteString) bencodes[i++], bencodes[i]);
        return dictionary;
    }

    public ByteChannel channel() {
        return new MultiByteChannel(List.of(new ByteBufferChannel((byte) 'd'), new MultiByteChannel(entrySet().stream().flatMap(entry -> Stream.of(entry.getKey(), entry.getValue())).map(Bencode::channel).iterator()), new ByteBufferChannel((byte) 'e')).iterator());
    }
}
