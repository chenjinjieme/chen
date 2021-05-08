package com.chen.test.maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class Local {
    private final Path local;
    private final Repository repository = new Repository();

    public Local(Path local) {
        this.local = local;
    }

    Path local() {
        return local;
    }

    Repository repository() {
        return repository;
    }

    private void find(Path path, Version version) throws IOException {
        var prefix = version.prefix();
        var length = prefix.length();
        try (var stream = Files.list(path)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (!Files.isDirectory(next)) {
                    var name = next.getFileName().toString();
                    if (name.endsWith(".jar") && name.startsWith(prefix)) {
                        var end = name.length() - 4;
                        if (end == length) version.classifier("", name);
                        else if (name.charAt(length) == '-') version.classifier(name.substring(length + 1, end), name);
                    }
                }
            }
        }
    }

    private void find(Iterator<Path> iterator, Artifact artifact) throws IOException {
        var name = artifact.name();
        while (iterator.hasNext()) {
            var next = iterator.next();
            if (Files.isDirectory(next)) {
                var file = next.getFileName().toString();
                if (Files.exists(next.resolve(name + "-" + file + ".pom"))) find(next, artifact.version(file));
            }
        }
    }

    private void find(Path path, Artifact artifact) throws IOException {
        try (var stream = Files.list(path)) {
            find(stream.iterator(), artifact);
        }
    }

    private void find(Path path, Group group, String name) throws IOException {
        if (Files.exists(path.resolve("maven-metadata.xml"))) find(path, group.artifact(name));
        else try (var stream = Files.list(path)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (Files.isDirectory(next)) {
                    var file = next.getFileName().toString();
                    if (Files.exists(next.resolve(name + "-" + file + ".pom"))) {
                        var artifact = group.artifact(name);
                        find(next, artifact.version(file));
                        find(iterator, artifact);
                    } else find(next, group.group(name), file);
                }
            }
        }
    }

    private void find(Path path, Group group) throws IOException {
        try (var stream = Files.list(path)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (Files.isDirectory(next)) find(next, group, next.getFileName().toString());
            }
        }
    }

    public Local find() throws IOException {
        System.out.println("find");
        try (var stream = Files.list(local)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (Files.isDirectory(next)) find(next, repository.group(next.getFileName().toString()));
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }

    private void resource(Collection<Path> collection, Resource resource) {
        collection.add(local.resolve(resource.path()));
        collection.add(local.resolve(resource.sha1()));
    }

    private boolean clean(Path path, Collection<Path> collection) throws IOException {
        System.out.printf("search %s\r", local.relativize(path));
        var count = 0;
        try (var stream = Files.list(path)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (Files.isDirectory(next)) {
                    if (!clean(next, collection)) count++;
                } else if (collection.contains(next)) {
                    count++;
                    System.out.printf("match %s\r", local.relativize(next));
                } else {
                    Files.delete(next);
                    System.out.printf("delete %s\n", local.relativize(next));
                }
            }
        }
        if (count == 0) {
            Files.delete(path);
            System.out.printf("delete %s\n", local.relativize(path));
            return true;
        } else return false;
    }

    public Local clean() throws IOException {
        System.out.println("clean");
        var set = new TreeSet<Path>();
        for (Group group : repository)
            for (Artifact artifact : group) {
                resource(set, artifact.metadata());
                for (Version version : artifact) {
                    resource(set, version.pom());
                    for (Classifier classifier : version) resource(set, classifier.jar());
                }
            }
        clean(local, set);
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }
}
