package com.chen.test.maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Local {
    private final Path local;
    private final Repository repository = new Repository();

    public Local(Path local) {
        this.local = local;
    }

    public Repository repository() {
        return repository;
    }

    public Local clean() throws IOException {
        System.out.println("clean");
        try (var stream = Files.walk(local)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                var name = next.getFileName().toString();
                if (name.equals("resolver-status.properties") || name.equals("_remote.repositories") || name.equals("maven-metadata-local.xml")) {
                    Files.delete(next);
                    System.out.printf("delete %s\n", local.relativize(next));
                } else System.out.printf("search %s\r", next);
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }

    private void find(Path path) throws IOException {
        try (var stream = Files.list(path)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                var file = next.getFileName().toString();
                if (Files.isDirectory(next)) find(next);
                else if (file.startsWith("maven-metadata-central.xml")) {
                    var artifactPath = local.relativize(next.getParent());
                    var group = repository.groupMap().computeIfAbsent(artifactPath.getParent(), Group::parse);
                    group.artifactMap().computeIfAbsent(artifactPath.getFileName().toString(), name -> new Artifact(group, name)).add(file);
                } else {
                    var versionPath = local.relativize(next.getParent());
                    var artifactPath = versionPath.getParent();
                    var group = repository.groupMap().computeIfAbsent(artifactPath.getParent(), Group::parse);
                    var artifact = group.artifactMap().computeIfAbsent(artifactPath.getFileName().toString(), name -> new Artifact(group, name));
                    artifact.versionMap().computeIfAbsent(versionPath.getFileName().toString(), name -> new Version(artifact, name)).add(file);
                }
                System.out.printf("search %s\r", next);
            }
        }
    }

    public Local find() throws IOException {
        System.out.println("find");
        find(local);
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }

    private void print(String prefix, Resource resource) {
        if (resource != null) {
            var name = resource.name();
            if (resource.resource()) System.out.printf(prefix + "%s\n", name);
            if (resource.sha1()) System.out.printf(prefix + "%s.sha1\n", name);
        }
    }

    public Local tree() {
        repository.groupMap().forEach((path, group) -> {
            System.out.println(group.name());
            group.artifactMap().forEach((artifactName, artifact) -> {
                System.out.printf("  %s\n", artifactName);
                print("    ", artifact.metadata());
                artifact.versionMap().forEach((versionName, version) -> {
                    System.out.printf("    %s\n", versionName);
                    print("      ", version.pom());
                    print("      ", version.jar());
                    version.classifierMap().forEach((classifierName, classifier) -> print("      ", classifier.jar()));
                });
            });
        });
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }

    public Local untracked() throws IOException {
        System.out.println("untracked");
        var collect = repository.groupMap().values().stream().flatMap(group -> group.artifactMap().values().stream()).flatMap(artifact -> Stream.concat(Stream.ofNullable(artifact.metadata()), artifact.versionMap().values().stream().flatMap(version -> Stream.concat(Stream.of(version.pom(), version.jar()), version.classifierMap().values().stream().map(Classifier::jar))))).filter(Objects::nonNull).flatMap(resource -> Stream.of(resource.path(), resource.sha1Path())).filter(Objects::nonNull).map(local::resolve).collect(Collectors.toSet());
        var list = new ArrayList<Path>();
        try (var stream = Files.walk(local).filter(path -> !Files.isDirectory(path))) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                var name = next.toString();
                if (!collect.contains(next)) {
                    if (name.endsWith(".lastUpdated") || name.endsWith(".part") || name.endsWith(".lock") || name.endsWith(".md5")) {
                        Files.delete(next);
                        System.out.printf("delete %s\n", local.relativize(next));
                    } else list.add(next);
                } else System.out.printf("search %s\r", next);
            }
        }
        list.forEach(path -> System.out.printf("untracked %s\n", local.resolve(path)));
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }

    private void empty(Path path) throws IOException {
        try (var stream = Files.list(path)) {
            var iterator = stream.iterator();
            if (!iterator.hasNext()) {
                Files.delete(path);
                System.out.printf("delete %s\n", local.relativize(path));
            } else do {
                var next = iterator.next();
                if (Files.isDirectory(next)) empty(next);
                System.out.printf("search %s\r", path);
            } while (iterator.hasNext());
        }
    }

    public Local empty() throws IOException {
        System.out.println("empty");
        empty(local);
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }
}
