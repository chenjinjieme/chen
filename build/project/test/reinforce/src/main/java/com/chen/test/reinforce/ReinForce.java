package com.chen.test.reinforce;

import com.chen.core.base.nio.file.Files;
import com.chen.core.base.security.MessageDigest;
import com.chen.file.torrent.Torrent;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ReinForce {
    private Path bt, n, base;

    public ReinForce(String path) {
        this(Path.of(path));
    }

    public ReinForce(Path path) {
        bt = path.resolve("bt");
        n = path.resolve("new");
        base = path.resolve("base.torrent");
    }

    public void create() throws IOException, NoSuchAlgorithmException {
        var torrent = Torrent.parse(base);
        var md5 = MessageDigest.md5();
        Files.list(bt, path -> {
            var name = path.getFileName();
            var resolve = n.resolve(name);
            var parse = Torrent.parse(path);
            torrent.info(parse.info()).creationDate(parse.creationDate()).write(resolve);
            if (!Arrays.equals(md5.digest(path), md5.digest(resolve))) System.out.println(name);
        });
    }

    public void chance() throws IOException {
        if (Files.count(n) == 0) throw new ReinForceException("new is null");
        Files.delete(bt);
        Files.move(n, bt);
        Files.createDirectories(n);
    }

    public void name() throws IOException {
        Files.list(bt, path -> {
            var file = path.getFileName().toString();
            if (!getName(file).equals(getName((Torrent.parse(path).info().name())))) System.out.println(file);
        });
    }

    private String getName(String file) {
        var index = file.indexOf(".");
        return index > 0 ? file.substring(0, index) : file;
    }

    public static class ReinForceException extends RuntimeException {
        private ReinForceException(String message) {
            super(message);
        }
    }
}
