package com.chen.test.maven;

public class Classifier {
    private final Version version;
    private final String name;
    private final Resource jar;

    public Classifier(Version version, String name, String file) {
        this.version = version;
        this.name = name;
        jar = new Resource(version.path(), file);
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
}
