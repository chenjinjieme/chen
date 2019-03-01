package com.chen.core.math;

import com.chen.core.lang.ByteSequence;

public abstract class HexUtil {
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final byte[] DECIMAL = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15};

    private static char[] getHex0(long x, char[] chars, int offset, int length) {
        for (var i = offset + length - 1; i >= offset; i--, x >>>= 4) chars[i] = HEX[((int) (x & 0xF))];
        return chars;
    }

    public static String getHex(int x) {
        return new String(getHex0(x, new char[8], 0, 8));
    }

    public static String getHex(byte[] bytes) {
        return getHex(bytes, 0, bytes.length);
    }

    public static String getHex(byte[] bytes, int offset, int length) {
        var chars = new char[length << 1];
        for (int i = offset, l = offset + length, count = -2; i < l; i++) getHex0(bytes[i], chars, count += 2, 2);
        return new String(chars);
    }

    public static String getHex(ByteSequence sequence) {
        return getHex(sequence.bytes(), sequence.offset(), sequence.length());
    }

    public static byte[] getBytes(String s) {
        var l = s.length();
        var bytes = new byte[l >>> 1];
        for (var i = 0; i < l; i += 2) bytes[i >>> 1] = (byte) (DECIMAL[s.charAt(i)] << 4 | DECIMAL[s.charAt(i | 1)]);
        return bytes;
    }
}
