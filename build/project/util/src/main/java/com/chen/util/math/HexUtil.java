package com.chen.util.math;

public abstract class HexUtil {
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final byte[] DECIMAL = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15};

    private static char[] getHex0(long x, char[] chars, int s, int l) {
        for (int i = s + l - 1; i >= s; i--, x >>>= 4) chars[i] = HEX[((int) (x & 0xF))];
        return chars;
    }

    public static String getHex(int x) {
        return new String(getHex0(x, new char[8], 0, 8));
    }

    public static String getHex(byte[] bytes) {
        char[] chars = new char[bytes.length << 1];
        int count = -2;
        for (int b : bytes) getHex0(b, chars, count += 2, 2);
        return new String(chars);
    }

    public static byte[] getBytes(String s) {
        int l = s.length();
        byte[] bytes = new byte[l >>> 1];
        for (int i = 0; i < l; i += 2) bytes[i >>> 1] = (byte) (DECIMAL[s.charAt(i)] << 4 | DECIMAL[s.charAt(i | 1)]);
        return bytes;
    }
}
