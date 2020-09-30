package com.chen.test.hash;

import com.chen.core.hex.Hexs;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Hash {
    private final Path path;
    private final Directory directory;
    private final Yaml yaml = new Yaml(new HashConstructor(), new HashRepresenter());
    private final Queue<MessageDigest> messageDigestQueue = new ConcurrentLinkedQueue<>();
    private final Queue<ByteBuffer> byteBufferQueue = new ConcurrentLinkedQueue<>();
    private CompletableFuture<Void> hashFuture = CompletableFuture.completedFuture(null);
    private CompletableFuture<Void> digestFuture = CompletableFuture.completedFuture(null);
    private final AtomicLong time = new AtomicLong();
    private final AtomicLong read = new AtomicLong();
    private final AtomicLong hash = new AtomicLong();
    private ScheduledExecutorService executor;

    public Hash(Path path) throws IOException {
        this.path = path;
        try (var reader = Files.newBufferedReader(path)) {
            directory = yaml.load(reader);
        }
    }

    private MessageDigest getDigest() throws NoSuchAlgorithmException {
        var digest = messageDigestQueue.poll();
        if (digest == null) digest = MessageDigest.getInstance("SHA-512");
        return digest;
    }

    private ByteBuffer getBuffer() {
        var buffer = byteBufferQueue.poll();
        if (buffer == null) buffer = ByteBuffer.allocateDirect(8192);
        return buffer;
    }

    private void reset() {
        time.set(System.currentTimeMillis());
        read.set(0);
        hash.set(0);
    }

    private String iostat() {
        var l = System.currentTimeMillis() - time.get();
        return String.format("%.2fm/s %.2fm/s", read.get() / 1048.576 / l, hash.get() / 1048.576 / l);
    }

    private Directory find(Path base) {
        var directory = this.directory;
        if (base != null && base.toString().length() > 0) for (int i = 0, l = base.getNameCount(); i < l; i++) directory = (Directory) directory.get(base.getName(i).toString());
        return directory;
    }

    private void hash(Path path, Consumer<String> consumer) {
        hashFuture = hashFuture.thenRunAsync(new FutureTask<>(() -> {
            var digest = getDigest();
            try (var channel = Files.newByteChannel(path)) {
                for (var buffer = getBuffer(); channel.read(buffer) > 0; buffer = getBuffer()) {
                    var byteBuffer = buffer;
                    read.addAndGet(buffer.position());
                    digestFuture = digestFuture.thenRunAsync(() -> {
                        digest.update(byteBuffer.flip());
                        hash.addAndGet(byteBuffer.position());
                        byteBufferQueue.offer(byteBuffer.clear());
                    });
                }
            }
            digestFuture = digestFuture.thenRunAsync(() -> {
                consumer.accept(Hexs.getHex(digest.digest()));
                messageDigestQueue.offer(digest);
            });
            return null;
        }));
    }

    private void digest() throws InterruptedException {
        if (!hashFuture.isDone() || !digestFuture.isDone()) synchronized (this) {
            hashFuture.thenRunAsync(() -> digestFuture.thenRunAsync(() -> {
                synchronized (this) {
                    this.notify();
                }
            }));
            this.wait();
        }
    }

    private void add(Directory parent, Path base, Path path) throws IOException {
        var name = path.getFileName().toString();
        var resolve = base.resolve(name);
        if (Files.isDirectory(path)) {
            var directory = (Directory) parent.computeIfAbsent(name, Directory::new);
            try (var list = Files.list(path)) {
                for (var iterator = list.iterator(); iterator.hasNext(); ) add(directory, resolve, iterator.next());
            }
        } else {
            var file = (File) parent.computeIfAbsent(name, key -> {
                var value = new File(key, "");
                System.out.printf("add %s %s\r", iostat(), resolve);
                return value;
            });
            if (file.hash().length() == 0) hash(path, hash -> {
                file.hash(hash);
                System.out.printf("hash %s %s: %s\r", iostat(), resolve, hash);
            });
        }
    }

    public void add(Path base, Path path) throws IOException, InterruptedException {
        System.out.println("add");
        System.out.println("----------------------------------------------------------------------------------------------------");
        reset();
        add(find(base), base, path);
        digest();
        System.out.println(iostat());
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public void move(Path base, Path path) {
        System.out.println("move");
        System.out.println("----------------------------------------------------------------------------------------------------");
        var map = find(base.getParent());
        var file = map.remove(base.getFileName().toString());
        if (file != null) {
            var name = path.getFileName().toString();
            if (find(path.getParent()).put(name, file.name(name)) != null) System.out.printf("overwrite %s\n", path);
            System.out.printf("move %s -> %s\n", base, path);
        } else System.out.printf("miss %s\n", base);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    private void remove0(Path base) {
        if (base.toString().length() == 0) {
            directory.clear();
            System.out.println("clear");
        } else {
            var map = find(base.getParent());
            if (map.remove(base.getFileName().toString()) != null) System.out.printf("remove %s\n", base);
        }
    }

    public void remove(Path base) {
        System.out.println("remove");
        System.out.println("----------------------------------------------------------------------------------------------------");
        remove0(base);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public void update(Path base, Path path) throws IOException, InterruptedException {
        System.out.println("update");
        System.out.println("----------------------------------------------------------------------------------------------------");
        remove0(base);
        var parent = base.getParent();
        if (parent == null) parent = Path.of("");
        reset();
        add(find(parent), parent, path);
        digest();
        System.out.println(iostat());
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    private void listBase(Path base, Directory directory, Set<Path> set) {
        directory.forEach((key, value) -> {
            var resolve = base.resolve(key);
            if (value instanceof Directory) listBase(resolve, (Directory) value, set);
            else set.add(resolve);
        });
    }

    private void listBase(Path base, Directory directory, Map<Path, String> map) {
        directory.forEach((key, value) -> {
            var resolve = base.resolve(key);
            if (value instanceof Directory) listBase(resolve, (Directory) value, map);
            else map.put(resolve, ((File) value).hash());
        });
    }

    private void listPath(Path root, Path path, Set<Path> set) throws IOException {
        try (var list = Files.list(path)) {
            for (var iterator = list.sorted(Comparator.comparing((Path file) -> !Files.isDirectory(file)).thenComparing(Path::compareTo)).iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (Files.isDirectory(next)) listPath(root, next, set);
                else set.add(root.relativize(next));
            }
        }
    }

    public void compare(Path base, Path path) throws IOException {
        System.out.println("compare");
        System.out.println("----------------------------------------------------------------------------------------------------");
        var baseSet = new LinkedHashSet<Path>();
        listBase(Path.of(""), find(base), baseSet);
        var pathSet = new LinkedHashSet<Path>();
        listPath(path, path, pathSet);
        baseSet.removeIf(pathSet::remove);
        System.out.println("+");
        for (var file : baseSet) System.out.println(file);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println("-");
        for (var file : pathSet) System.out.println(file);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public void check(Path base, Path path) throws InterruptedException {
        System.out.println("check");
        System.out.println("----------------------------------------------------------------------------------------------------");
        var baseMap = new LinkedHashMap<Path, String>();
        listBase(Path.of(""), find(base), baseMap);
        var change = new LinkedHashMap<Path, String>();
        var miss = new LinkedHashSet<Path>();
        reset();
        for (var entry : baseMap.entrySet()) {
            var key = entry.getKey();
            var file = path.resolve(key);
            if (!Files.exists(file)) {
                System.out.printf("miss %s %s\r", iostat(), key);
                miss.add(key);
            } else hash(file, hash -> {
                if (!entry.getValue().equals(hash)) {
                    System.out.printf("change %s %s: %s\r", iostat(), key, hash);
                    change.put(key, hash);
                } else System.out.printf("hash %s %s: %s\r", iostat(), key, hash);
            });
        }
        digest();
        System.out.println(iostat());
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (change.size() == 0) System.out.println("change none");
        else change.forEach((key, value) -> System.out.printf("change %s: %s -> %s\n", key, baseMap.get(key), value));
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (miss.size() == 0) System.out.println("miss none");
        else miss.forEach(value -> System.out.printf("miss %s: %s\n", value, baseMap.get(value)));
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    private boolean equals(Path path1, Path path2) throws IOException {
        try (var channel1 = FileChannel.open(path1); var channel2 = FileChannel.open(path2)) {
            if (channel1.size() != channel2.size()) return false;
            var buffer1 = getBuffer();
            var buffer2 = getBuffer();
            for (var read = channel1.read(buffer1); read > 0; read = channel1.read(buffer1)) {
                channel2.read(buffer2);
                if (!buffer1.flip().equals(buffer2.flip())) return false;
            }
            byteBufferQueue.offer(buffer1.clear());
            byteBufferQueue.offer(buffer2.clear());
        }
        return true;
    }

    public void link(Path base, Path path, Path disk) throws IOException {
        System.out.println("link");
        System.out.println("----------------------------------------------------------------------------------------------------");
        var baseMap = new LinkedHashMap<Path, String>();
        var link = 0;
        var skip = 0;
        var miss = new LinkedHashMap<Path, String>();
        var same = new LinkedHashMap<Path, String>();
        var find = new LinkedHashMap<Path, String>();
        listBase(Path.of(""), find(base), baseMap);
        for (var entry : baseMap.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var file = path.resolve(key);
            var hash = disk.resolve(value.substring(0, 2)).resolve(value);
            if (!Files.exists(file)) {
                miss.put(key, value);
                System.out.printf("miss %s: %s\n", key, value);
            } else if (!Files.exists(hash)) {
                Files.createDirectories(hash.getParent());
                Files.createLink(hash, file);
                link++;
                System.out.printf("link %s: %s\r", key, value);
            } else if (Files.isSameFile(file, hash)) {
                skip++;
                System.out.printf("skip %s: %s\r", key, value);
            } else if (equals(file, hash)) {
                Files.delete(file);
                Files.createLink(file, hash);
                same.put(key, value);
                System.out.printf("same %s: %s\n", key, value);
            } else {
                find.put(key, value);
                System.out.printf("find %s: %s\n", key, value);
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("link %d\n", link);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("skip %d\n", skip);
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (miss.size() == 0) System.out.println("miss none");
        else miss.forEach((name, digest) -> System.out.printf("miss %s: %s\n", name, digest));
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (same.size() == 0) System.out.println("same none");
        else same.forEach((name, digest) -> System.out.printf("same %s: %s\n", name, digest));
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (find.size() == 0) System.out.println("find none");
        else find.forEach((name, digest) -> System.out.printf("find %s: %s\n", name, digest));
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public void hash(Path base, Path path, Path find) throws IOException, InterruptedException {
        System.out.println("hash");
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (!Files.exists(find)) {
            if (!Files.exists(path) && Files.exists(base)) Files.move(base, path);
            if (Files.exists(path)) {
                var size = new AtomicInteger();
                var total = new AtomicInteger();
                var check = new AtomicInteger();
                reset();
                try (var stream = Files.list(path)) {
                    for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                        var next = iterator.next();
                        size.incrementAndGet();
                        try (var list = Files.list(next)) {
                            var count = new AtomicInteger();
                            var finish = new AtomicBoolean();
                            for (var pathIterator = list.iterator(); pathIterator.hasNext(); ) {
                                var file = pathIterator.next();
                                count.incrementAndGet();
                                total.incrementAndGet();
                                hash(file, hash -> new FutureTask<>(() -> {
                                    var name = file.getFileName().toString();
                                    var get = check.incrementAndGet();
                                    if (name.equals(hash)) {
                                        Files.delete(file);
                                        System.out.printf("check %s %d %d/%d %s\r", iostat(), size.get(), get, total.get(), hash);
                                    } else {
                                        Files.createDirectories(find);
                                        Files.move(file, find.resolve(name));
                                        System.out.printf("find %s %s: %s\n", iostat(), name, hash);
                                    }
                                    if (count.decrementAndGet() == 0 && finish.get()) {
                                        Files.delete(next);
                                        size.decrementAndGet();
                                    }
                                    return null;
                                }).run());
                            }
                            finish.set(true);
                            if (count.get() == 0) {
                                Files.delete(next);
                                size.decrementAndGet();
                            }
                        }
                    }
                }
                digest();
                System.out.println(iostat());
                if (size.get() == 0) Files.delete(path);
            } else System.out.println("null");
        } else System.out.println("find");
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println();
    }

    public void dump() throws IOException {
        try (var write = Files.newBufferedWriter(path)) {
            yaml.dump(directory, write);
            System.out.println("-->save");
        }
    }

    public void autoDump(int delay) {
        var runnable = (Runnable) () -> new FutureTask<>(() -> {
            dump();
            return null;
        }).run();
        if (executor == null) Runtime.getRuntime().addShutdownHook(new Thread(runnable));
        else close();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(runnable, delay, delay, TimeUnit.SECONDS);
    }

    public void close() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }
}
