package com.chen.test.hash;

import java.util.AbstractMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

public class Directory extends AbstractMap<String, Path> implements Path {
    private String name;
    private final TreeMap<String, Path> map;

    public Directory(String name) {
        this.name = name;
        map = new TreeMap<>();
    }

    public String name() {
        return name;
    }

    public Directory name(String name) {
        this.name = name;
        return this;
    }

    public Path get(Object key) {
        return map.get(key);
    }

    public Path put(String key, Path value) {
        return map.put(key, value);
    }

    public Path remove(Object key) {
        return map.remove(key);
    }

    public Set<Entry<String, Path>> entrySet() {
        return map.entrySet();
    }

    public Path computeIfAbsent(String key, Function<? super String, ? extends Path> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    public boolean equals(Object o) {
        return this == o || o instanceof Path && name.equals(((Path) o).name());
    }

    public int hashCode() {
        return name.hashCode();
    }
}
