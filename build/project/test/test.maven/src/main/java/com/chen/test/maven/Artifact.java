package com.chen.test.maven;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Artifact implements Iterable<Version> {
    private final Group group;
    private final String name;
    private final Path path;
    private final Resource metadata;
    private final Map<String, Version> versionMap = new TreeMap<>();

    public Artifact(Group group, String name) {
        this.group = group;
        this.name = name;
        path = group.path().resolve(name);
        metadata = new Resource(path, "maven-metadata.xml");
    }

    public Group group() {
        return group;
    }

    public String name() {
        return name;
    }

    public Path path() {
        return path;
    }

    public Resource metadata() {
        return metadata;
    }

    public Version version(String name) {
        return versionMap.computeIfAbsent(name, version -> new Version(this, version));
    }

    public Iterator<Version> iterator() {
        return versionMap.values().iterator();
    }
}
