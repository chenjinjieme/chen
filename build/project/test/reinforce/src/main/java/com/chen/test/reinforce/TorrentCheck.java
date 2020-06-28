package com.chen.test.reinforce;

import com.chen.core.nio.file.Files;
import com.chen.core.security.MessageDigest;
import com.chen.core.util.function.Consumer3;
import com.chen.file.torrent.File;
import com.chen.file.torrent.Torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TorrentCheck {
    private Torrent torrent;

    public TorrentCheck(Torrent torrent) {
        this.torrent = torrent;
    }

    public TorrentCheck pre(Path path) throws IOException {
        var paths = new LinkedHashSet<Path>();
        var files = new TreeSet<Path>();
        var info = torrent.info();
        var name = info.name();
        var base = path.resolve(name);
        var torrent = new AtomicInteger();
        var ass = new AtomicInteger();
        for (var file : info.files()) paths.add(base.resolve(file.path()));
        var bak = new LinkedHashSet<>(paths);
        Files.walk(base, file -> {
            if (!Files.isDirectory(file))
                if (paths.contains(file)) paths.remove(file);
                else {
                    var fileName = file.getFileName().toString();
                    var index = fileName.lastIndexOf('.');
                    var extension = fileName.substring(index + 1);
                    if (extension.equals("torrent")) {
                        if (fileName.substring(0, index).equals(name)) {
                            torrent.getAndIncrement();
                            return;
                        }
                    } else if (extension.equals("ass")) {
                        if (bak.contains(file.resolveSibling(fileName.substring(0, index) + ".mkv"))) {
                            ass.getAndIncrement();
                            return;
                        }
                    }
                    files.add(file);
                }
        });
        synchronized (TorrentCheck.class) {
            System.out.println();
            System.out.println(name);
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("- %s\n", paths.size());
            for (var file : paths) System.out.println(file);
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println("torrent " + torrent.get());
            System.out.println("ass " + ass.get());
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("+ %s\n", files.size());
            for (var file : files) System.out.println(file);
            System.out.println("----------------------------------------------------------------------------------------------------");
        }
        return this;
    }

    public TorrentCheck build(Path path) throws IOException {
        var info = torrent.info();
        var base = path.resolve(info.name());
        var create = new ArrayList<File>();
        var reset = new ArrayList<File>();
        for (var file : info.files()) {
            var name = base.resolve(file.path());
            Files.createDirectories(name.getParent());
            if (!Files.exists(name)) create.add(file);
            else if (Files.size(name) != file.length()) reset.add(file);
            else continue;
            Files.setLength(name, file.length());
        }
        synchronized (TorrentCheck.class) {
            System.out.println("create");
            for (var file : create) System.out.println(file.path());
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println("reset");
            for (var file : reset) System.out.println(file.path());
            System.out.println("----------------------------------------------------------------------------------------------------");
        }
        return this;
    }

    public TorrentCheck check(Path path, int nThreads) throws ExecutionException, InterruptedException {
        return check(path, Executors.newFixedThreadPool(nThreads));
    }

    public TorrentCheck check(Path path, ExecutorService executorService) throws ExecutionException, InterruptedException {
        var info = torrent.info();
        var pieces = info.pieces();
        var pieceLength = info.pieceLength();
        var name = info.name();
        System.out.println(name);
        System.out.println("----------------------------------------------------------------------------------------------------");
        var base = path.resolve(name);
        var size = pieces.size();
        var count = new AtomicInteger();
        var checked = new AtomicInteger();
        var scheduledFutures = new ScheduledFuture<?>[1];
        scheduledFutures[0] = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            var i = count.get();
            var j = checked.get();
            System.out.printf("%s/%s/%s %.2f%%/%.2f%%%n", j, i, size, j * 100d / size, i * 100d / size);
            if (i == size) scheduledFutures[0].cancel(true);
        }, 0, 1, SECONDS);
        var futures = new ArrayList<Future<Object>>(size);
        var bufferThreadLocal = ThreadLocal.withInitial(() -> ByteBuffer.allocate(pieceLength));
        var messageDigestThreadLocal = ThreadLocal.withInitial(MessageDigest::sha1);
        for (var piece : pieces)
            futures.add(executorService.submit(() -> {
                var offset = piece.offset();
                var read = 0;
                var buffer = bufferThreadLocal.get().clear();
                for (var file : piece.files()) {
                    try (var channel = FileChannel.open(base.resolve(file.path()))) {
                        read += channel.position(offset).read(buffer);
                    }
                    offset = 0;
                }
                var digest = messageDigestThreadLocal.get().digest(buffer.array(), 0, read);
                var sequence = piece.buffer();
                if (Arrays.equals(digest, 0, digest.length, sequence.array(), sequence.position(), sequence.limit())) {
                    piece.checked(true);
                    checked.getAndIncrement();
                }
                count.getAndIncrement();
                return null;
            }));
        for (var future : futures) future.get();
        try {
            scheduledFutures[0].get();
        } catch (CancellationException ignored) {
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (var piece : pieces)
            if (!piece.checked()) {
                System.out.println(piece.index());
                for (var file : piece.files()) System.out.println(file.path());
                System.out.println("----------------------------------------------------------------------------------------------------");
            }
        return this;
    }

    public static void preAll(Path path) throws IOException, ExecutionException, InterruptedException {
        var pool = Executors.newCachedThreadPool();
        var futures = new ArrayList<Future<Object>>();
        Files.walk(path, file -> {
            futures.add(pool.submit(() -> {
                if (file.getFileName().toString().endsWith(".torrent")) new TorrentCheck(Torrent.parse(file)).pre(file.getParent().getParent());
                return null;
            }));
        });
        for (var future : futures) future.get();
    }

    public static void checkAll(Path path, int nThreads) throws IOException, ExecutionException, InterruptedException {
        var fixedThreadPool = Executors.newFixedThreadPool(nThreads);
        Files.walk(path, (Consumer3<Path, IOException, ExecutionException, InterruptedException>) file -> {
            if (file.getFileName().toString().endsWith(".torrent")) new TorrentCheck(Torrent.parse(file)).check(file.getParent().getParent(), fixedThreadPool);
        });
    }
}
