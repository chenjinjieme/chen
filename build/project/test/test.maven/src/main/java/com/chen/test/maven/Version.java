package com.chen.test.maven;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Version implements Iterable<Classifier> {
    private final Artifact artifact;
    private final String name;
    private final String prefix;
    private final Path path;
    private final Resource pom;
    private final Map<String, Classifier> classifierMap = new TreeMap<>();

    public Version(Artifact artifact, String name) {
        this.artifact = artifact;
        this.name = name;
        prefix = artifact.name() + '-' + name;
        path = artifact.path().resolve(name);
        pom = new Resource(path, prefix + ".pom");
    }

    public Artifact artifact() {
        return artifact;
    }

    public String name() {
        return name;
    }

    public String prefix() {
        return prefix;
    }

    public Path path() {
        return path;
    }

    public Resource pom() {
        return pom;
    }

    public Classifier classifier(String name, String file) {
        return classifierMap.computeIfAbsent(name, classifier -> new Classifier(this, classifier, file));
    }

    public Iterator<Classifier> iterator() {
        return classifierMap.values().iterator();
    }
}
