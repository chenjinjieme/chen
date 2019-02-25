package com.chen.file.torrent;

import com.chen.core.base.math.HexUtil;
import com.chen.core.base.nio.ByteBuffer;
import com.chen.core.base.nio.file.Files;
import com.chen.core.base.security.MessageDigest;
import com.chen.core.bencode.Integer;
import com.chen.core.bencode.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

public class Torrent extends Dictionary {
    public static final ByteString INFO = new ByteString("info");
    public static final ByteString CREATION_DATE = new ByteString("creation date");
    public static final ByteString FILES = new ByteString("files");
    public static final ByteString LENGTH = new ByteString("length");
    public static final ByteString PATH = new ByteString("path");
    public static final ByteString NAME = new ByteString("name");
    public static final ByteString PIECE_LENGTH = new ByteString("piece length");
    public static final ByteString PIECES = new ByteString("pieces");

    private Torrent(ByteBuffer buffer) {
        super(Dictionary.parse(buffer));
    }

    public Info info() {
        var info = ((Dictionary) get(INFO));
        return info instanceof Info ? (Info) info : new Info(info);
    }

    public Torrent info(Info info) {
        put(INFO, info);
        return this;
    }

    public Integer creationDate() {
        return (Integer) get(CREATION_DATE);
    }

    public Torrent creationDate(Integer integer) {
        put(CREATION_DATE, integer);
        return this;
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
        return parse(Path.of(path));
    }

    public static Torrent parse(Path path) throws IOException {
        return parse(ByteBuffer.wrap(Files.readAllBytes(path)));
    }

    public void write(String path) throws IOException {
        write(Path.of(path));
    }

    public void write(Path path) throws IOException {
        var buffer = ByteBuffer.allocate(length());
        write(buffer);
        Files.write(path, buffer.array());
    }

    public void build(Path path) throws IOException {
        var info = info();
        var base = path.resolve(info.name());
        for (var file : info.files()) {
            var name = base.resolve(file.path());
            Files.createDirectories(name.getParent());
            new RandomAccessFile(name.toFile(), "rw").setLength(file.fileLength());
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

            public Path path() {
                var path = Path.of("");
                for (var value : ((List) get(PATH))) path = path.resolve(value.toString());
                return path;
            }

            public long fileLength() {
                return ((Integer) get(LENGTH)).value();
            }
        }
    }
}
