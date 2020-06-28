package com.chen.file.torrent;

import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Dictionary;
import com.chen.core.hex.Hexs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Torrent {
    public static final ByteString INFO = new ByteString("info");
    public static final ByteString CREATION_DATE = new ByteString("creation date");
    private Dictionary dictionary;

    public Torrent(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary dictionary() {
        return dictionary;
    }

    public Info info() {
        return new Info((Dictionary) dictionary.get(INFO));
    }

    public String hash() throws NoSuchAlgorithmException {
        var info = dictionary.get(INFO);
        var buffer = ByteBuffer.allocate(info.bufferSize());
        info.write(buffer);
        return Hexs.getHex(MessageDigest.getInstance("SHA-1").digest(buffer.array()));
    }

    public static Torrent parse(ByteBuffer buffer) {
        return new Torrent(Dictionary.parse(buffer));
    }

    public static Torrent parse(Path path) throws IOException {
        return parse(ByteBuffer.wrap(Files.readAllBytes(path)));
    }

    public Torrent write(Path path) throws IOException {
        var buffer = ByteBuffer.allocate(dictionary.bufferSize());
        dictionary.write(buffer);
        Files.write(path, buffer.array());
        return this;
    }
}
