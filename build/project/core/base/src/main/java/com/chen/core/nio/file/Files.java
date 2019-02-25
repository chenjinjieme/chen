package com.chen.core.nio.file;

import com.chen.core.util.function.Consumer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.CopyOption;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
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

    public static byte[] readAllBytes(Path path) throws IOException {
        return java.nio.file.Files.readAllBytes(path);
    }

    public static Path write(Path path, byte[] bytes, OpenOption... options) throws IOException {
        return java.nio.file.Files.write(path, bytes, options);
    }

    public static boolean isDirectory(Path path, LinkOption... options) {
        return java.nio.file.Files.isDirectory(path, options);
    }

    public static Stream<Path> list(Path dir) throws IOException {
        return java.nio.file.Files.list(dir);
    }

    public static void list(Path path, Consumer<Path, IOException> consumer) throws IOException {
        try (var list = list(path)) {
            for (Iterator<Path> iterator = list.iterator(); iterator.hasNext(); ) consumer.accept(iterator.next());
        }
    }

    public static long count(Path path) throws IOException {
        try (var list = list(path)) {
            return list.count();
        }
    }
}
