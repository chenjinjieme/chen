package com.chen.test.reinforce;

import com.chen.file.torrent.Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ReinForce {
    private final Path bt;
    private final Path n;
    private final Path base;

    public ReinForce(Path path) {
        bt = path.resolve("bt");
        n = path.resolve("new");
        base = path.resolve("base.torrent");
    }

    public void create() throws IOException {
        try (var list = Files.list(bt)) {
            for (var iterator = list.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (next.toString().endsWith(".torrent")) {
                    var torrent = Torrent.parse(base);
                    var name = next.getFileName();
                    var resolve = n.resolve(name);
                    var parse = Torrent.parse(next);
                    var dst = torrent.dictionary();
                    var src = parse.dictionary();
                    Optional.ofNullable(src.get(Torrent.INFO)).ifPresent(value -> dst.put(Torrent.INFO, value));
                    Optional.ofNullable(src.get(Torrent.CREATION_DATE)).ifPresent(value -> dst.put(Torrent.CREATION_DATE, value));
                    torrent.write(resolve);
                    try (var channel1 = FileChannel.open(next); var channel2 = FileChannel.open(resolve)) {
                        var size = channel1.size();
                        if (channel2.size() == size) {
                            var buffer1 = ByteBuffer.allocate(8192);
                            var buffer2 = ByteBuffer.allocate(8192);
                            for (; size > 0; ) {
                                size -= channel1.read(buffer1.clear());
                                channel2.read(buffer2.clear());
                                if (!buffer1.flip().equals(buffer2.flip())) {
                                    System.out.println(name);
                                    break;
                                }
                            }
                        } else System.out.println(name);
                    }
                }
            }
        }
    }

    public void chance() throws IOException {
        try (var list = Files.list(n)) {
            if (list.count() == 0) throw new RuntimeException("new is null");
        }
        try (var list = Files.list(bt)) {
            for (var iterator = list.iterator(); iterator.hasNext(); ) Files.delete(iterator.next());
        }
        Files.delete(bt);
        Files.move(n, bt);
        Files.createDirectories(n);
    }

    public void name() throws IOException {
        try (var list = Files.list(bt)) {
            for (var iterator = list.iterator(); iterator.hasNext(); ) {
                var path = iterator.next();
                if (path.toString().endsWith(".torrent")) {
                    var file = path.getFileName().toString();
                    var name = Torrent.parse(path).info().name();
                    if (!file.substring(0, file.length() - 8).equals(name)) {
                        System.out.println(file);
                        System.out.println(name);
                    }
                }
            }
        }
    }
}
