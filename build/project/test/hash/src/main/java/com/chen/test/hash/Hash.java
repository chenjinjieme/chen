package com.chen.test.hash;

import com.chen.core.hex.Hexs;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Hash {
    private static final Path DS_STORE = Path.of(".DS_Store");
    private Yaml yaml;
    private Path path;
    private Directory directory;
    private MessageDigest digest;
    private ScheduledFuture<?> future;

    public Hash(Path path) throws IOException {
        try (var reader = Files.newBufferedReader(path)) {
            var options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            yaml = new Yaml(options);
            this.path = path;
            (directory = new Directory("")).addAllPath(yaml.load(reader));
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Directory find(Path base) {
        var directory = this.directory;
        if (base != null && base.toString().length() > 0) for (int i = 0, l = base.getNameCount(); i < l; i++) directory = directory.getDirectory(base.getName(i).toString());
        return directory;
    }

    private String hash(Path path) throws IOException {
        try (var channel = Files.newByteChannel(path)) {
            var buffer = ByteBuffer.allocate(8192);
            for (; channel.read(buffer.clear()) > 0; ) digest.update(buffer.flip());
            return Hexs.getHex(digest.digest());
        }
    }

    private void add(Directory parent, Path base, Path path) throws IOException {
        if (path.endsWith(DS_STORE)) return;
        var name = path.getFileName().toString();
        var resolve = base.resolve(name);
        if (Files.isDirectory(path)) {
            var directory = Optional.ofNullable(parent.getDirectory(name)).orElse(parent.newDirectory(name));
            try (var list = Files.list(path)) {
                for (var iterator = list.iterator(); iterator.hasNext(); ) add(directory, resolve, iterator.next());
            }
        } else if (parent.getFile(name) == null) {
            var hash = hash(path);
            parent.newFile(name, hash);
            System.out.printf("add %s: %s\n", resolve, hash);
        }
    }

    public void add(Path base, Path path) throws IOException {
        System.out.println("add");
        System.out.println("----------------------------------------------------------------------------------------------------");
        add(find(base), base, path);
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public void move(Path base, Path path) {
        System.out.println("move");
        System.out.println("----------------------------------------------------------------------------------------------------");
        var map = find(base.getParent());
        var file = map.getPath(base.getFileName().toString());
        if (file != null) {
            map.removePath(file);
            find(path.getParent()).addPath(file.name(path.getFileName().toString()));
            System.out.printf("move %s -> %s\n", base, path);
        } else System.out.printf("miss %s\n", base);
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public void remove(Path base) {
        System.out.println("remove");
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (base.toString().length() == 0) {
            directory.clear();
            System.out.println("clear");
            System.out.println("----------------------------------------------------------------------------------------------------");
            return;
        }
        var map = find(base.getParent());
        if (map.removePath(base.getFileName().toString())) System.out.printf("remove %s\n", base);
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public void update(Path base, Path path) throws IOException {
        System.out.println("update");
        System.out.println("----------------------------------------------------------------------------------------------------");
        remove(base);
        add(base.getParent(), path);
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
            else map.put(resolve, value.toString());
        });
    }

    private void listPath(Path root, Path path, Set<Path> set) throws IOException {
        try (var list = Files.list(path)) {
            for (var iterator = list.sorted(Comparator.comparing((Function<Path, Boolean>) Files::isDirectory).reversed().thenComparing(Path::compareTo)).iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (!next.endsWith(DS_STORE)) if (Files.isDirectory(next)) listPath(root, next, set);
                else set.add(Path.of(root.relativize(next).toString()));
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
        baseSet.removeIf(file -> {
            var contains = pathSet.contains(file);
            if (contains) pathSet.remove(file);
            return contains;
        });
        System.out.println("+");
        for (var file : baseSet) System.out.println(file);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println("-");
        for (var file : pathSet) System.out.println(file);
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public void check(Path base, Path path) throws IOException {
        System.out.println("check");
        System.out.println("----------------------------------------------------------------------------------------------------");
        var baseMap = new LinkedHashMap<Path, String>();
        listBase(Path.of(""), find(base), baseMap);
        var change = new LinkedHashMap<Path, String>();
        var miss = new LinkedHashMap<Path, String>();
        for (var entry : baseMap.entrySet()) {
            var key = entry.getKey();
            var file = path.resolve(key);
            if (Files.exists(file)) {
                var hash = hash(file);
                if (!entry.getValue().equals(hash)) {
                    System.out.printf("change %s: %s\n", key, hash);
                    change.put(key, hash);
                } else System.out.printf("hash %s: %s\n", key, hash);
            } else {
                System.out.printf("miss %s\n", key);
                miss.put(key, null);
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (change.size() == 0) System.out.println("change none");
        else change.forEach((key, value) -> System.out.printf("change %s: %s -> %s\n", key, baseMap.get(key), value));
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (miss.size() == 0) System.out.println("miss none");
        else miss.forEach((key, value) -> System.out.printf("miss %s: %s -> %s\n", key, baseMap.get(key), value));
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public void link(Path base, Path path, Path disk) throws IOException {
        System.out.println("link");
        System.out.println("----------------------------------------------------------------------------------------------------");
        disk = disk.resolve("hash");
        var baseMap = new LinkedHashMap<Path, String>();
        var link = 0;
        var skip = 0;
        var same = new LinkedHashMap<Path, String>();
        var find = new LinkedHashMap<Path, String>();
        listBase(Path.of(""), find(base), baseMap);
        for (var entry : baseMap.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var file = path.resolve(key);
            var hash = disk.resolve(value);
            if (!Files.exists(hash)) {
                Files.createLink(hash, file);
                link++;
                System.out.printf("link %s->%s\n", key, value);
            } else if (!Files.isSameFile(file, hash)) if (hash(file).equals(value)) {
                Files.delete(file);
                Files.createLink(file, hash);
                same.put(key, value);
                System.out.printf("same %s->%s\n", key, value);
            } else {
                find.put(key, value);
                System.out.printf("find %s->%s\n", key, value);
            }
            else {
                skip++;
                System.out.printf("skip %s->%s\n", key, value);
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("link %d\n", link);
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("skip %d\n", skip);
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (same.size() == 0) System.out.println("same none");
        else same.forEach((name, md5) -> System.out.printf("same %s -> %s\n", name, md5));
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (find.size() == 0) System.out.println("find none");
        else find.forEach((name, md5) -> System.out.printf("find %s -> %s\n", name, md5));
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public void dump() throws IOException {
        try (var write = Files.newBufferedWriter(path)) {
            yaml.dump(directory, write);
            System.out.println("-->save");
        }
    }

    public void autoDump(int delay) {
        if (future != null) return;
        future = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try {
                dump();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, delay, delay, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                dump();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public void close() {
        if (future == null) return;
        future.cancel(false);
        future = null;
    }
}
