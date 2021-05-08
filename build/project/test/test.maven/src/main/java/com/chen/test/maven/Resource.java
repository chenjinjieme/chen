package com.chen.test.maven;

import java.nio.file.Path;

public class Resource {
    private final String name;
    private final Path path;
    private final Path sha1;

    public Resource(Path path, String name) {
        this.name = name;
        this.path = path.resolve(name);
        sha1 = path.resolve(name + ".sha1");
    }

    public String name() {
        return name;
    }

    public Path path() {
        return path;
    }

    public Path sha1() {
        return sha1;
    }
}
