package com.chen.core.bencode;

import com.chen.core.nio.channels.ByteBufferChannel;
import com.chen.core.nio.channels.MultiByteChannel;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List extends ArrayList<Bencode> implements Bencode {
    public static List parse(ByteBuffer buffer) {
        var list = new List();
        for (var b = buffer.position(buffer.position() + 1).get(buffer.position()); b != 'e'; b = buffer.get(buffer.position())) list.add(Bencode.parse(buffer));
        buffer.position(buffer.position() + 1);
        return list;
    }

    public static List of(Bencode... bencodes) {
        return Stream.of(bencodes).collect(Collectors.toCollection(List::new));
    }

    public ByteChannel channel() {
        return new MultiByteChannel(java.util.List.of(new ByteBufferChannel((byte) 'l'), new MultiByteChannel(stream().map(Bencode::channel).iterator()), new ByteBufferChannel((byte) 'e')).iterator());
    }
}
