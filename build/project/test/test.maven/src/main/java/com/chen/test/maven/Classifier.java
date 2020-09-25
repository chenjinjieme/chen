package com.chen.test.maven;

import java.nio.file.Path;

public class Classifier {
    private final Version version;
    private final String name;
    private Resource jar;
    private final Path path;

    public Classifier(Version version, String name) {
        this.version = version;
        this.name = name;
        path = version.path();
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
        var prefix = version.artifact().name() + '-' + version.name() + '-' + name;
        if (jar == null) jar = new Resource(prefix + ".jar", path);
        var suffix = file.substring(prefix.length() + 4);
        if (suffix.equals("")) jar.resource(true);
        else if (suffix.equals(".sha1")) jar.sha1(true);
        return this;
    }
}
