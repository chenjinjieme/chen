package com.chen.test.maven;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class Version {
    private final Artifact artifact;
    private final String name;
    private final Map<String, Classifier> classifierMap = new TreeMap<>();
    private Resource pom;
    private Resource jar;
    private final Path path;

    public Version(Artifact artifact, String name) {
        this.artifact = artifact;
        this.name = name;
        path = artifact.path().resolve(name);
    }

    public Artifact artifact() {
        return artifact;
    }

    public String name() {
        return name;
    }

    Map<String, Classifier> classifierMap() {
        return classifierMap;
    }

    public Resource pom() {
        return pom;
    }

    public Resource jar() {
        return jar;
    }

    public Path path() {
        return path;
    }

    public Version add(String file) {
        var prefix = artifact.name() + '-' + name;
        var suffix = file.substring(prefix.length());
        if (suffix.charAt(0) == '.') {
            var resource = suffix.startsWith(".pom") ? pom == null ? pom = new Resource(prefix + ".pom", path) : pom : suffix.startsWith(".jar") ? jar == null ? jar = new Resource(prefix + ".jar", path) : jar : null;
            suffix = suffix.substring(4);
            if (suffix.equals("")) resource.resource(true);
            else if (suffix.equals(".sha1")) resource.sha1(true);
        } else classifierMap.computeIfAbsent(suffix.substring(1, suffix.indexOf(".")), name -> new Classifier(this, name)).add(file);
        return this;
    }
}
