package com.chen.test.maven;

import java.nio.file.Path;

public class Classifier {
    private final Version version;
    private final String name;
    private final Path path;
    private final Resource jar;

    public Classifier(Version version, String name) {
        this.version = version;
        this.name = name;
        path = version.path();
        jar = new Resource(version.artifact().name() + '-' + version.name() + '-' + name + ".jar", path);
    }

    public Version version() {
        return version;
    }

    public String name() {
        return name;
    }

    public Resource jar() {
        return jar;
    }

    public Path path() {
        return path;
    }

    public Classifier add(String file) {
        var suffix = file.substring(version.artifact().name().length() + version.name().length() + name.length() + 6);
        if (suffix.equals("")) jar.resource(true);
        else if (suffix.equals(".sha1")) jar.sha1(true);
        return this;
    }
}
