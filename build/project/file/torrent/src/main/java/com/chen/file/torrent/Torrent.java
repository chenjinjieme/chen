package com.chen.file.torrent;

import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Dictionary;
import com.chen.core.bencode.Integer;
import com.chen.core.math.HexUtil;
import com.chen.core.nio.ByteBuffer;
import com.chen.core.nio.file.Files;
import com.chen.core.security.MessageDigest;

import java.io.IOException;
import java.nio.file.Path;

public class Torrent extends Dictionary {
    public static final ByteString INFO = new ByteString("info");
    public static final ByteString CREATION_DATE = new ByteString("creation date");

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

    public String hash() {
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

    public Torrent write(String path) throws IOException {
        return write(Path.of(path));
    }

    public Torrent write(Path path) throws IOException {
        var buffer = ByteBuffer.allocate(length());
        write(buffer);
        Files.write(path, buffer.array());
        return this;
    }
}
