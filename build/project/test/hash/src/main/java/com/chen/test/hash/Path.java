package com.chen.test.hash;

import java.util.Map;

public interface Path extends Map.Entry<String, Object>, Comparable<Path> {
    String name();

    Path name(String name);

    default String getKey() {
        return name();
    }

    default Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }
}
