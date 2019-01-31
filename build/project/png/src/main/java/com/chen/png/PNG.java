package com.chen.png;

import com.chen.base.zip.CRC;
import com.chen.base.zip.ZlibUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PNG {
    private static final long HEAD = 0x89504E470D0A1A0AL;
    private Chunk IHDR;
    private List<Chunk> IDATs;
    private List<Chunk> others;

    public PNG(File file) throws IOException {
        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            if (!(inputStream.readLong() == HEAD && (IHDR = new Chunk(inputStream)).type.equals("IHDR")))
                throw new RuntimeException("not a png file");
            IDATs = new ArrayList<>();
            others = new ArrayList<>();
            for (; ; ) {
                Chunk chunk = new Chunk(inputStream);
                switch (chunk.type) {
                    case "IDAT":
                        IDATs.add(chunk);
                        break;
                    case "IEND":
                        return;
                    default:
                        others.add(chunk);
                }
            }
        }
    }

    public PNG(String path) throws IOException {
        this(new File(path));
    }

    public byte[] getIDAT() {
        ZlibUtil.Inflater inflater = ZlibUtil.inflater();
        for (Chunk chunk : IDATs) inflater.add(chunk.data);
        return inflater.getBytes();
    }

    public PNG redeflater() {
        IDATs = Collections.singletonList(new Chunk("IDAT", ZlibUtil.deflater(getIDAT()).getBytes()));
        return this;
    }

    public PNG setIDATSize(int size) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (Chunk chunk : IDATs) outputStream.write(chunk.data, 0, chunk.data.length);
        byte[] bytes = outputStream.toByteArray();
        int l = bytes.length;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        IDATs = new ArrayList<>();
        for (byte[] data; l > 0; ) {
            int i = Integer.min(size, l);
            l -= inputStream.read(data = new byte[i], 0, i);
            IDATs.add(new Chunk("IDAT", data));
        }
        return this;
    }

    public void write(OutputStream outputStream) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream))) {
            dataOutputStream.writeLong(HEAD);
            IHDR.write(dataOutputStream);
            for (Chunk chunk : IDATs) chunk.write(dataOutputStream);
            Chunk.IEND.write(dataOutputStream);
        }
    }

    private static class Chunk {
        private static final Chunk IEND = new Chunk("IEND", new byte[0]);
        private String type;
        private byte[] data;
        private int crc;

        private Chunk(String type, byte[] data) {
            this.type = type;
            this.data = data;
            crc = new CRC(type).update(data).getValue();
        }

        private Chunk(DataInputStream inputStream) throws IOException {
            int length = inputStream.readInt();
            byte[] bytes = new byte[4];
            inputStream.read(bytes);
            type = new String(bytes);
            inputStream.read(data = new byte[length]);
            if ((crc = inputStream.readInt()) != new CRC(type).update(data).getValue())
                throw new RuntimeException();
        }

        private void write(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeInt(data.length);
            dataOutputStream.write(type.getBytes());
            dataOutputStream.write(data);
            dataOutputStream.writeInt(crc);
        }
    }
}
