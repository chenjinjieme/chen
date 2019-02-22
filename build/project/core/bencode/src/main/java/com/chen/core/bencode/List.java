package com.chen.core.bencode;

import com.chen.core.base.nio.ByteBuffer;
import com.chen.core.base.util.WrappedList;

import java.util.ArrayList;

public class List extends WrappedList<Value> implements Value {
    public List(java.util.List<Value> list) {
        super(list);
    }

    private List(ByteBuffer buffer) {
        super(new ArrayList<>());
        if (buffer.get() != 'l') throw new ParseException("not a list");
        for (int b = buffer.get(buffer.position()); b != 'e'; b = buffer.get(buffer.position())) add(Value.parse(buffer));
        buffer.position(buffer.position() + 1);
    }

    public int length() {
        int length = 2;
        for (Value value : this) length += value.length();
        return length;
    }

    public static List parse(ByteBuffer buffer) {
        return new List(buffer);
    }

    public void write(ByteBuffer buffer) {
        buffer.put((byte) 'l');
        for (Value value : this) value.write(buffer);
        buffer.put((byte) 'e');
    }
}
