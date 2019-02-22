package com.chen.file.torrent;

import com.chen.core.base.math.HexUtil;
import com.chen.core.base.nio.ByteBuffer;
import com.chen.core.base.security.MessageDigest;
import com.chen.core.bencode.Integer;
import com.chen.core.bencode.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

import static java.nio.ByteBuffer.wrap;

public class Torrent extends Dictionary {
    public static final ByteString INFO = new ByteString("info"), CREATION_DATE = new ByteString("creation date"), FILES = new ByteString("files"), LENGTH = new ByteString("length"), PATH = new ByteString("path"), NAME = new ByteString("name"), PIECE_LENGTH = new ByteString("piece length"), PIECES = new ByteString("pieces");

    private Torrent(ByteBuffer buffer) {
        super(Dictionary.parse(buffer));
    }

    public Info info() {
        var info = ((Dictionary) get(INFO));
        return info instanceof Info ? (Info) info : new Info(info);
    }

    public String hash() throws NoSuchAlgorithmException {
        var info = get(INFO);
        var buffer = ByteBuffer.allocate(info.length());
        info.write(buffer);
        return HexUtil.getHex(MessageDigest.sha1().digest(buffer.array()));
    }

    public static Torrent parse(ByteBuffer buffer) {
        return new Torrent(buffer);
    }

    public static Torrent parse(String path) throws IOException {
        return parse(new File(path));
    }

    public static Torrent parse(File file) throws IOException {
        try (var channel = new FileInputStream(file).getChannel()) {
            var buffer = ByteBuffer.allocate(((int) channel.size()));
            channel.read(wrap(buffer.array()));
            return parse(buffer);
        }
    }

    public void write(String path) throws IOException {
        write(new File(path));
    }

    public void write(File file) throws IOException {
        try (var channel = new FileOutputStream(file).getChannel()) {
            var buffer = ByteBuffer.allocate(length());
            write(buffer);
            channel.write(wrap(buffer.array()));
        }
    }

    public void build(File path) throws IOException {
        var info = info();
        var base = new File(path, info.name());
        for (var file : info.files()) {
            var name = new File(base, file.path());
            name.getParentFile().mkdirs();
            new RandomAccessFile(name, "rw").setLength(file.fileLength());
        }
    }

    public class Info extends Dictionary {
        private Info(Dictionary dictionary) {
            super(dictionary);
        }

        public String name() {
            return get(NAME).toString();
        }

        public java.util.List<File> files() {
            var files = ((List) get(FILES));
            if (files == null) return java.util.List.of(new File(Map.of(LENGTH, Info.this.get(LENGTH), PATH, new List(java.util.List.of()))));
            else {
                var list = new ArrayList<File>(files.size());
                for (var file : files) list.add(new File((Dictionary) file));
                return list;
            }
        }

        public class File extends Dictionary {
            private File(Map<ByteString, Value> map) {
                super(map);
            }

            public String path() {
                var builder = new StringBuilder();
                for (var path : ((List) get(PATH))) builder.append('/').append(path);
                return builder.toString();
            }

            public long fileLength() {
                return ((Integer) get(LENGTH)).value();
            }
        }
    }
}
