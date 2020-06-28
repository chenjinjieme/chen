package com.chen.test.hash;

import java.util.*;

public class Directory extends AbstractMap<String, Object> implements Path {
    private String name;
    private TreeSet<Path> set;

    public Directory(String name) {
        this.name = name;
        set = new TreeSet<>();
    }

    private Directory(String name, TreeSet<Path> set) {
        this.name = name;
        this.set = set;
    }

    public String name() {
        return name;
    }

    public Directory name(String name) {
        this.name = name;
        return this;
    }

    public File newFile(String name, String hash) {
        var file = new File(name, hash);
        set.add(file);
        return file;
    }

    public Directory newDirectory(String name) {
        var directory = new Directory(name);
        set.add(directory);
        return directory;
    }

    private Path getPath(Path path) {
        return Optional.ofNullable(set.floor(path)).filter(file -> file.name().equals(path.name())).orElse(null);
    }

    public File getFile(String name) {
        return (File) getPath(new File(name, null));
    }

    public Directory getDirectory(String name) {
        return (Directory) getPath(new Directory(name, null));
    }

    public Path getPath(String name) {
        return Optional.ofNullable(getPath(new Directory(name, null))).orElse(getPath(new File(name, null)));
    }

    public boolean addPath(Path path) {
        return set.add(path);
    }

    public boolean removePath(Path path) {
        return set.remove(path);
    }

    public boolean removePath(String name) {
        return removePath(new Directory(name, null)) || removePath(new File(name, null));
    }

    public void addAllPath(Map<String, Object> map) {
        map.forEach((key, value) -> {
            if (value instanceof String) newFile(key, value.toString());
            else newDirectory(key).addAllPath((Map<String, Object>) value);
        });
    }

    public Set<Entry<String, Object>> entrySet() {
        return (Set) set;
    }

    public Object getValue() {
        return this;
    }

    public int compareTo(Path o) {
        return o instanceof File ? -1 : name.compareToIgnoreCase(o.name());
    }
}
