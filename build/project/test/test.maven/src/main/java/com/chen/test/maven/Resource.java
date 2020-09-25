package com.chen.test.maven;

import java.nio.file.Path;

public class Resource {
    private final String name;
    private boolean resource;
    private boolean sha1;
    private final Path path;
    private final Path sha1Path;

    public Resource(String name, Path path) {
        this.name = name;
        this.path = path.resolve(name);
        sha1Path = path.resolve(name + ".sha1");
    }

    public String name() {
        return name;
    }

    public boolean resource() {
        return resource;
    }

    Resource resource(boolean resource) {
        this.resource = resource;
        return this;
    }

    public boolean sha1() {
        return sha1;
    }

    Resource sha1(boolean sha1) {
        this.sha1 = sha1;
        return this;
    }

    public Path path() {
        return path;
    }

    public Path sha1Path() {
        return sha1Path;
    }
}
