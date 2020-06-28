package com.chen.core.bencode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class List extends ArrayList<Value> implements Value {
    public static List parse(ByteBuffer buffer) {
        var list = IntStream.iterate(buffer.position(buffer.position() + 1).get(buffer.position()), b -> b != 'e', b -> buffer.get(buffer.position())).mapToObj(b -> Value.parse(buffer)).collect(Collectors.toCollection(List::new));
        buffer.position(buffer.position() + 1);
        return list;
    }

    public static List of(Value... values) {
        return Stream.of(values).collect(Collectors.toCollection(List::new));
    }

    public int bufferSize() {
        return stream().mapToInt(Value::bufferSize).sum() + 2;
    }

    public void write(ByteBuffer buffer) {
        buffer.put((byte) 'l');
        forEach(value -> value.write(buffer));
        buffer.put((byte) 'e');
    }
}
