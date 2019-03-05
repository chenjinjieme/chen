package com.chen.core.nio.file;

import com.chen.core.util.function.Consumer3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.stream.Stream;

public class Files {
    public static Path setLength(Path path, long length) throws IOException {
        new RandomAccessFile(path.toFile(), "rw").setLength(length);
        return path;
    }

    public static Path createDirectories(Path path, FileAttribute<?>... attrs) throws IOException {
        return java.nio.file.Files.createDirectories(path, attrs);
    }

    public static void delete(Path path) throws IOException {
        if (isDirectory(path)) list(path, Files::delete);
        java.nio.file.Files.delete(path);
    }

    public static Path move(Path source, Path target, CopyOption... options) throws IOException {
        return java.nio.file.Files.move(source, target, options);
    }

    public static boolean isDirectory(Path path, LinkOption... options) {
        return java.nio.file.Files.isDirectory(path, options);
    }

    public static long size(Path path) throws IOException {
        return java.nio.file.Files.size(path);
    }

    public static boolean exists(Path path, LinkOption... options) {
        return java.nio.file.Files.exists(path, options);
    }

    public static byte[] readAllBytes(Path path) throws IOException {
        return java.nio.file.Files.readAllBytes(path);
    }

    public static Path write(Path path, byte[] bytes, OpenOption... options) throws IOException {
        return java.nio.file.Files.write(path, bytes, options);
    }

    public static Stream<Path> list(Path dir) throws IOException {
        return java.nio.file.Files.list(dir);
    }

    public static <E extends Throwable, F extends Throwable, G extends Throwable> void list(Path path, Consumer3<Path, E, F, G> consumer) throws IOException, E, F, G {
        try (var list = list(path)) {
            for (Iterator<Path> iterator = list.iterator(); iterator.hasNext(); ) consumer.accept(iterator.next());
        }
    }

    public static Stream<Path> walk(Path start, FileVisitOption... options) throws IOException {
        return java.nio.file.Files.walk(start, options);
    }

    public static <E extends Throwable, F extends Throwable, G extends Throwable> void walk(Path path, Consumer3<Path, E, F, G> consumer, FileVisitOption... options) throws IOException, E, F, G {
        try (var list = walk(path, options)) {
            for (Iterator<Path> iterator = list.iterator(); iterator.hasNext(); ) consumer.accept(iterator.next());
        }
    }

    public static long count(Path path) throws IOException {
        try (var list = list(path)) {
            return list.count();
        }
    }
}
