package com.chen.core.bencode;

import java.util.stream.Stream;

class Values {
    static int length(long l) {
        return (int) Stream.iterate(10L, i -> l >= i, i -> i * 10).count() + 1;
    }
}
