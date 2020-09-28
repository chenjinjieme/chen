package com.chen.test.maven;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class Artifact {
    private final Group group;
    private final String name;
    private final Map<String, Version> versionMap = new TreeMap<>();
    private final Path path;
    private final Resource metadata;

    public Artifact(Group group, String name) {
        this.group = group;
        this.name = name;
        path = group.path().resolve(name);
        metadata = new Resource("maven-metadata-central.xml", path);
    }

    public Group group() {
        return group;
    }

    public String name() {
        return name;
    }

    Map<String, Version> versionMap() {
        return versionMap;
    }

    public Resource metadata() {
        return metadata;
    }

    public Path path() {
        return path;
    }

    public Artifact add(String file) {
        var suffix = file.substring(26);
        if (suffix.equals("")) metadata.resource(true);
        else if (suffix.equals(".sha1")) metadata.sha1(true);
        return this;
    }
}
