package com.chen.test.reinforce;

import com.chen.core.base.security.MessageDigest;
import com.chen.core.bencode.ByteString;
import com.chen.file.torrent.Torrent;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import static com.chen.file.torrent.Torrent.CREATION_DATE;
import static com.chen.file.torrent.Torrent.INFO;

public class ReinForce {
    private File bt, n, base;

    public ReinForce(String resources) {
        bt = new File(resources, "bt");
        n = new File(resources, "new");
        base = new File(resources, "base.torrent");
    }

    public void create() throws IOException, NoSuchAlgorithmException {
        var torrent = Torrent.parse(base);
        for (File file : Objects.requireNonNull(bt.listFiles())) {
            var parse = Torrent.parse(file);
            torrent.put(CREATION_DATE, parse.get(CREATION_DATE));
            torrent.put(INFO, parse.get(INFO));
            torrent.write(new File(n, file.getName()));
        }
        var md5 = MessageDigest.md5();
        for (File file : Objects.requireNonNull(bt.listFiles())) if (!Arrays.equals(md5.digest(file), md5.digest(new File(n, file.getName())))) System.out.println(file.getName());
    }

    public void chance() {
        if (Objects.requireNonNull(n.listFiles()).length == 0) throw new RuntimeException();
        for (File file : Objects.requireNonNull(bt.listFiles())) file.delete();
        bt.delete();
        n.renameTo(bt);
        n.mkdirs();
    }

    public void name() throws IOException {
        for (File file : Objects.requireNonNull(bt.listFiles()))
            if (!getName(file.getName()).equals(getName((Torrent.parse(file).info().get(new ByteString("name")).toString())))) System.out.println(file.getName());
    }

    private String getName(String file) {
        var index = file.indexOf(".");
        return index > 0 ? file.substring(0, index) : file;
    }
}
