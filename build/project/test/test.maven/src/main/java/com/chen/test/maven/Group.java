package com.chen.test.maven;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class Group {
    private final String name;
    private final Map<String, Artifact> artifactMap = new TreeMap<>();
    private final Path path;

    public Group(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String name() {
        return name;
    }

    Map<String, Artifact> artifactMap() {
        return artifactMap;
    }

    public Path path() {
        return path;
    }

    public static Group parse(Path path) {
        var count = path.getNameCount();
        var builder = new StringBuilder(path.getName(0).toString());
        for (var i = 1; i < count; i++) builder.append('.').append(path.getName(i).toString());
        return new Group(builder.toString(), path);
    }
}
