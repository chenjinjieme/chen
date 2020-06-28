package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

public class Dictionary extends LinkedHashMap<ByteString, Value> implements Value {
    public static Dictionary parse(ByteBuffer buffer) {
        var dictionary = new Dictionary();
        for (var b = buffer.position(buffer.position() + 1).get(buffer.position()); b != 'e'; b = buffer.get(buffer.position())) dictionary.put(ByteString.parse(buffer), Value.parse(buffer));
        buffer.position(buffer.position() + 1);
        return dictionary;
    }

    public static Dictionary of(Value... values) {
        var dictionary = new Dictionary();
        for (var i = 0; i < values.length; i++) dictionary.put((ByteString) values[i], values[++i]);
        return dictionary;
    }

    public int bufferSize() {
        return entrySet().stream().flatMap(entry -> Stream.of(entry.getKey(), entry.getValue())).mapToInt(Value::bufferSize).sum() + 2;
    }

    public void write(ByteBuffer buffer) {
        buffer.put((byte) 'd');
        forEach((key, value) -> {
            key.write(buffer);
            value.write(buffer);
        });
        buffer.put((byte) 'e');
    }
}
