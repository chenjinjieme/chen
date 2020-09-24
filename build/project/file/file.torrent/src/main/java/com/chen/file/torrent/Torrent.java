package com.chen.file.torrent;

import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Dictionary;
import com.chen.core.hex.Hexs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Torrent {
    public static final ByteString INFO = new ByteString("info");
    public static final ByteString CREATION_DATE = new ByteString("creation date");
    private final Dictionary dictionary;

    public Torrent(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary dictionary() {
        return dictionary;
    }

    public Info info() {
        return new Info((Dictionary) dictionary.get(INFO));
    }

    public String hash() throws IOException, NoSuchAlgorithmException {
        var buffer = ByteBuffer.allocate(8192);
        var digest = MessageDigest.getInstance("SHA-1");
        var channel = dictionary.get(INFO).channel();
        for (var read = channel.read(buffer.clear()); read >= 0; read = channel.read(buffer.clear())) digest.update(buffer.flip());
        return Hexs.getHex(digest.digest());
    }

    public static Torrent parse(ByteBuffer buffer) {
        return new Torrent(Dictionary.parse(buffer));
    }

    public static Torrent parse(Path path) throws IOException {
        try (var open = FileChannel.open(path)) {
            var buffer = ByteBuffer.allocate((int) open.size());
            open.read(buffer);
            return parse(buffer.flip());
        }
    }

    public Torrent write(Path path) throws IOException {
        var buffer = ByteBuffer.allocate(8192);
        try (var open = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            var channel = dictionary.channel();
            for (var read = channel.read(buffer.clear()); read >= 0; read = channel.read(buffer.clear())) open.write(buffer.flip());
        }
        return this;
    }
}
