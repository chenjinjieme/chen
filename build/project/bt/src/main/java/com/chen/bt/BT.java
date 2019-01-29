package com.chen.bt;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BT {
    private final Map<Bytes, Object> map;

    public BT(String path) throws IOException {
        try (Buffer buffer = new Buffer(path)) {
            if (buffer.get() == 'd') map = getMap(buffer);
            else throw new RuntimeException("not a bt");
        }
    }

    private Bytes getString(Buffer buffer) {
        int l = 0;
        for (int read; (read = buffer.get()) != ':'; ) l = l * 10 + read - '0';
        return new Bytes(buffer.get(new byte[l]));
    }

    private long getLong(Buffer buffer) {
        long i = 0;
        for (int read; (read = buffer.get()) != 'e'; ) i = i * 10 + read - '0';
        return i;
    }

    private List<Object> getList(Buffer buffer) {
        List<Object> list = new ArrayList<>();
        for (; buffer.get() != 'e'; ) list.add(getValue(buffer.back()));
        return list;
    }

    private Map<Bytes, Object> getMap(Buffer buffer) {
        Map<Bytes, Object> map = new LinkedHashMap<>();
        for (; buffer.get() != 'e'; ) map.put(getString(buffer.back()), getValue(buffer));
        return map;
    }

    private Object getValue(Buffer buffer) {
        byte b = buffer.get();
        if (b == 'i') return getLong(buffer);
        else if (b == 'l') return getList(buffer);
        else if (b == 'd') return getMap(buffer);
        else return getString(buffer.back());
    }

    public Map<Bytes, Object> getMap() {
        return map;
    }

    private void writeString(OutputStream outputStream, Bytes s) throws IOException {
        byte[] bytes = s.getBytes();
        outputStream.write(Integer.valueOf(bytes.length).toString().getBytes());
        outputStream.write(':');
        outputStream.write(bytes);
    }

    private void writeLong(OutputStream outputStream, Long i) throws IOException {
        outputStream.write('i');
        outputStream.write(i.toString().getBytes());
        outputStream.write('e');
    }

    private void writeList(OutputStream outputStream, List<Object> list) throws IOException {
        outputStream.write('l');
        for (Object o : list) writeValue(outputStream, o);
        outputStream.write('e');
    }

    private void writeMap(OutputStream outputStream, Map<Bytes, Object> map) throws IOException {
        outputStream.write('d');
        for (Map.Entry<Bytes, Object> entry : map.entrySet()) {
            writeString(outputStream, entry.getKey());
            writeValue(outputStream, entry.getValue());
        }
        outputStream.write('e');
    }

    private void writeValue(OutputStream outputStream, Object o) throws IOException {
        if (o instanceof Bytes) writeString(outputStream, (Bytes) o);
        else if (o instanceof Long) writeLong(outputStream, (Long) o);
        else if (o instanceof List) writeList(outputStream, (List) o);
        else if (o instanceof Map) writeMap(outputStream, (Map) o);
    }

    public void write(String path) throws IOException {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path))) {
            writeMap(outputStream, map);
        }
    }

    public static class Bytes {
        private final byte[] bytes;
        private final String string;

        public Bytes(byte[] bytes) {
            this.bytes = bytes;
            string = new String(bytes);
        }

        public Bytes(String s) {
            bytes = s.getBytes();
            string = s;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public String toString() {
            return string;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Bytes && string.equals(((Bytes) obj).string));
        }

        public int hashCode() {
            return string.hashCode();
        }
    }

    private static class Buffer implements Closeable {
        private final RandomAccessFile randomAccessFile;
        private final MappedByteBuffer mappedByteBuffer;

        private Buffer(String path) throws IOException {
            randomAccessFile = new RandomAccessFile(path, "r");
            try {
                mappedByteBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
            } catch (IOException e) {
                randomAccessFile.close();
                throw e;
            }
        }

        private byte get() {
            return mappedByteBuffer.get();
        }

        private byte[] get(byte[] bytes) {
            mappedByteBuffer.get(bytes);
            return bytes;
        }

        private Buffer back() {
            mappedByteBuffer.position(mappedByteBuffer.position() - 1);
            return this;
        }

        public void close() throws IOException {
            randomAccessFile.close();
        }
    }
}
