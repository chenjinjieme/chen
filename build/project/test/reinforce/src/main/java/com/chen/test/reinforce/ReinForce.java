package com.chen.test.reinforce;

import com.chen.core.nio.file.Files;
import com.chen.file.torrent.Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Optional;

import static com.chen.file.torrent.Torrent.CREATION_DATE;
import static com.chen.file.torrent.Torrent.INFO;

public class ReinForce {
    private Path bt, n, base;

    public ReinForce(Path path) {
        bt = path.resolve("bt");
        n = path.resolve("new");
        base = path.resolve("base.torrent");
    }

    public void create() throws IOException {
        Files.list(bt, path -> {
            var torrent = Torrent.parse(base);
            var name = path.getFileName();
            if (!name.toString().endsWith(".torrent")) return;
            var resolve = n.resolve(name);
            var parse = Torrent.parse(path);
            var dest = torrent.dictionary();
            var src = parse.dictionary();
            Optional.ofNullable(src.get(INFO)).ifPresent(value -> dest.put(INFO, value));
            Optional.ofNullable(src.get(CREATION_DATE)).ifPresent(value -> dest.put(CREATION_DATE, value));
            torrent.write(resolve);
            try (var channel1 = FileChannel.open(path); var channel2 = FileChannel.open(resolve)) {
                var l = channel1.size();
                if (channel2.size() != l) System.out.println(name);
                else {
                    var buffer1 = ByteBuffer.allocate(1024);
                    var buffer2 = ByteBuffer.allocate(1024);
                    for (; l > 0; ) {
                        l -= channel1.read(buffer1.clear());
                        channel2.read(buffer2.clear());
                        if (!buffer1.flip().equals(buffer2.flip())) {
                            System.out.println(name);
                            break;
                        }
                    }
                }
            }
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
            if (!file.endsWith(".torrent")) return;
            var name = Torrent.parse(path).info().name();
            if (!file.replace(".torrent", "").equals(name)) {
                System.out.println(file);
                System.out.println(name);
            }
        });
    }
}
