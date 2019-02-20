package com.chen.file.torrent;

import com.chen.core.base.math.HexUtil;
import com.chen.core.base.nio.ByteBuffer;
import com.chen.core.base.security.MessageDigest;
import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static java.nio.ByteBuffer.wrap;

public class Torrent extends Dictionary {
    public static final ByteString INFO = new ByteString("info"), CREATION_DATE = new ByteString("creation date");

    private Torrent(ByteBuffer buffer) {
        super(Dictionary.parse(buffer));
    }

    public Info info() {
        return new Info(this);
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
}
