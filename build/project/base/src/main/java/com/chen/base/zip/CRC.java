package com.chen.base.zip;

import java.util.zip.CRC32;

public class CRC {
    private CRC32 crc32 = new CRC32();

    public CRC() {
    }

    public CRC(byte[] bytes) {
        update(bytes);
    }

    public CRC(String s) {
        update(s);
    }

    public CRC update(byte[] bytes) {
        crc32.update(bytes);
        return this;
    }

    public CRC update(String s) {
        return update(s.getBytes());
    }

    public int getValue() {
        return (int) crc32.getValue();
    }
}
