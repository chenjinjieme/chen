package com.chen.core.bencode;

import com.chen.core.base.nio.ByteBuffer;
import com.chen.core.base.util.WrappedMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class Dictionary extends WrappedMap<ByteString, Value> implements Value {
    public Dictionary(Map<ByteString, Value> map) {
        super(map);
    }

    private Dictionary(ByteBuffer buffer) {
        super(new LinkedHashMap<>());
        if (buffer.get() != 'd') throw new ParseException("not a dictionary");
        for (int b = buffer.get(buffer.position()); b != 'e'; b = buffer.get(buffer.position())) put(ByteString.parse(buffer), Value.parse(buffer));
        buffer.position(buffer.position() + 1);
    }

    public int length() {
        int length = 2;
        for (Entry<ByteString, Value> entry : this.entrySet()) length += entry.getKey().length() + entry.getValue().length();
        return length;
    }

    public static Dictionary parse(ByteBuffer buffer) {
        return new Dictionary(buffer);
    }

    public void write(ByteBuffer buffer) {
        buffer.put((byte) 'd');
        for (Entry<ByteString, Value> entry : entrySet()) {
            entry.getKey().write(buffer);
            entry.getValue().write(buffer);
        }
        buffer.put((byte) 'e');
    }
}
