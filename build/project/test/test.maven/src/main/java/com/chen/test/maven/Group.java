package com.chen.test.maven;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Group implements Iterable<Artifact> {
    private final String name;
    private final Path path;
    private final Map<String, Group> groupMap = new TreeMap<>();
    private final Map<String, Artifact> artifactMap = new TreeMap<>();

    public Group(String name) {
        this.name = name;
        path = Path.of(name);
    }

    public Group(Group group, String name) {
        this.name = group.name + "." + name;
        path = group.path.resolve(name);
    }

    public String name() {
        return name;
    }

    public Path path() {
        return path;
    }

    public Group group(String name) {
        return groupMap.computeIfAbsent(name, group -> new Group(this, group));
    }

    public Artifact artifact(String name) {
        return artifactMap.computeIfAbsent(name, artifact -> new Artifact(this, artifact));
    }

    public GroupIterator groupIterator() {
        return new GroupIterator(groupMap.values().iterator());
    }

    public Iterator<Artifact> iterator() {
        return artifactMap.values().iterator();
    }
}
