package com.chen.base.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public abstract class ZlibUtil {
    public static Deflater deflater() {
        return new Deflater();
    }

    public static Deflater deflater(byte[] bytes) {
        return deflater().add(bytes);
    }

    public static Inflater inflater() {
        return new Inflater();
    }

    public static Inflater inflater(byte[] bytes) {
        return inflater().add(bytes);
    }

    private static abstract class Zlib {
        ByteArrayOutputStream byteArrayOutputStream;
        OutputStream outputStream;

        @SuppressWarnings("unchecked")
        public <T extends ZlibUtil.Zlib> T add(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return (T) this;
        }

        public byte[] getBytes() {
            try {
                outputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Deflater extends Zlib {
        private Deflater() {
            outputStream = new DeflaterOutputStream(byteArrayOutputStream = new ByteArrayOutputStream(), new java.util.zip.Deflater(9));
        }
    }

    public static class Inflater extends Zlib {
        private Inflater() {
            outputStream = new InflaterOutputStream(byteArrayOutputStream = new ByteArrayOutputStream());
        }
    }
}
