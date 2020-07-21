package com.chen.test.reinforce;

import com.chen.file.torrent.File;
import com.chen.file.torrent.Torrent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TorrentCheck {
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final Path DS_STORE = Path.of(".DS_Store");
    private final Torrent torrent;

    public TorrentCheck(Torrent torrent) {
        this.torrent = torrent;
    }

    private void pre(Path path, Set<Path> paths, Set<Path> files) throws IOException {
        try (var list = Files.list(path)) {
            for (var iterator = list.iterator(); iterator.hasNext(); ) {
                var file = Path.of(iterator.next().toString());
                if (Files.isDirectory(file)) pre(file, paths, files);
                else if (paths.contains(file)) paths.remove(file);
                else if (!file.endsWith(DS_STORE)) files.add(file);
            }
        }
    }

    public TorrentCheck pre(Path path) throws IOException {
        var paths = new LinkedHashSet<Path>();
        var files = new TreeSet<Path>();
        var info = torrent.info();
        var name = info.name();
        var base = path.resolve(name);
        for (var file : info.files()) paths.add(base.resolve(file.path()));
        pre(base, paths, files);
        synchronized (TorrentCheck.class) {
            System.out.println();
            System.out.println(name);
            System.out.println();
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.printf("- %s\n", paths.size());
            for (var file : paths) System.out.println(file);
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
            new RandomAccessFile(name.toFile(), "rw").setLength(file.length());
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

    private void check(ThreadLocal<MessageDigest> local, ByteBuffer buffer, ByteBuffer hash, AtomicInteger checked, AtomicInteger count, ByteBufferQueue queue) {
        POOL.submit(() -> {
            var digest = local.get();
            digest.update(buffer);
            var bytes = digest.digest();
            var match = true;
            for (var j = 0; j < 20; j++)
                if (bytes[j] != hash.get()) {
                    match = false;
                    break;
                }
            if (match) checked.incrementAndGet();
            count.incrementAndGet();
            queue.offer(buffer.clear());
        });
    }

    public TorrentCheck check(Path path) throws IOException {
        var info = torrent.info();
        var pieces = info.pieces();
        var pieceLength = info.pieceLength();
        var name = info.name();
        System.out.println(name);
        System.out.println("----------------------------------------------------------------------------------------------------");
        var base = path.resolve(name);
        var size = pieces.size();
        var checked = new AtomicInteger();
        var count = new AtomicInteger();
        var time = new AtomicInteger();
        var d = pieceLength / (1048576.0);
        var scheduledFuture = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            var x = checked.get();
            var y = count.get();
            var z = time.incrementAndGet();
            System.out.printf("%s/%s/%s %.2f%%/%.2f%% %.2fM/s%n", x, y, size, x * 100d / size, y * 100d / size, y * d / z);
        }, 0, 1, SECONDS);
        var local = ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        });
        var queue = new ByteBufferQueue(pieceLength);
        var buffer = queue.poll();
        var i = 0;
        var remaining = 0L;
        for (var file : info.files()) {
            var resolve = base.resolve(file.path());
            var offset = remaining;
            remaining += file.length();
            if (!Files.exists(resolve)) {
                System.out.printf("miss %s\n", resolve);
                for (; remaining > 0; remaining -= pieceLength, i++) count.incrementAndGet();
                buffer.clear();
            } else if (remaining > 0) try (var open = FileChannel.open(resolve)) {
                if (offset < 0) open.position(-offset);
                for (; remaining > pieceLength; remaining -= pieceLength, i++) {
                    open.read(buffer);
                    if (buffer.position() < pieceLength) {
                        System.out.printf("less %s\n", resolve);
                        for (; remaining > pieceLength; remaining -= pieceLength, i++) count.incrementAndGet();
                        count.incrementAndGet();
                        buffer.clear();
                    } else {
                        check(local, buffer.flip(), pieces.get(i).hash().duplicate(), checked, count, queue);
                        buffer = queue.poll();
                    }
                }
                if (remaining > 0) {
                    open.read(buffer);
                    var position = buffer.position();
                    if (position < remaining) {
                        System.out.printf("less %s\n", resolve);
                        remaining -= pieceLength;
                        i++;
                        count.incrementAndGet();
                        buffer.clear();
                    } else if (position > remaining) {
                        System.out.printf("long %s\n", resolve);
                        buffer.position((int) remaining);
                    }
                }
            }
        }
        if (remaining > 0) check(local, buffer.flip(), pieces.get(i).hash().duplicate(), checked, count, queue);
        synchronized (this) {
            try {
                for (; count.get() < size; ) this.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            scheduledFuture.cancel(false);
        } catch (CancellationException ignored) {
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        var x = checked.get();
        var y = count.get();
        var z = time.get();
        System.out.printf("%s/%s/%s %.2f%%/%.2f%% %.2fM/s%n", x, y, size, x * 100d / size, y * 100d / size, y * d / z);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
        return this;
    }

    private static void doAll(Path path, Path base, BiConsumer consumer) throws IOException {
        try (var list = Files.list(path).filter(Files::isDirectory)) {
            for (var iterator = list.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                var resolve = base.resolve(next.getFileName().toString() + ".torrent");
                if (Files.exists(resolve)) consumer.accept(new TorrentCheck(Torrent.parse(resolve)), path);
                else doAll(next, base, consumer);
            }
        }
    }

    public static void preAll(Path path, Path base) throws IOException {
        doAll(path, base, TorrentCheck::pre);
    }

    public static void checkAll(Path path, Path base) throws IOException {
        doAll(path, base, TorrentCheck::check);
    }

    private interface BiConsumer {
        void accept(TorrentCheck check, Path parent) throws IOException;
    }

    private static class ByteBufferQueue {
        private final Queue<ByteBuffer> queue;
        private final int length;

        private ByteBufferQueue(int capacity) {
            queue = new ConcurrentLinkedQueue<>();
            this.length = capacity;
        }

        private ByteBuffer poll() {
            return Optional.ofNullable(queue.poll()).orElseGet(() -> ByteBuffer.allocate(length));
        }

        private void offer(ByteBuffer buffer) {
            queue.offer(buffer);
        }
    }
}
