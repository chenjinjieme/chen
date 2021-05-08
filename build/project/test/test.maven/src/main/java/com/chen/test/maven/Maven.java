package com.chen.test.maven;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class Maven {
    private final Local local;
    private final Remote remote;

    public Maven(Local local, Remote remote) {
        this.local = local;
        this.remote = remote;
    }

    private void check(Path path, Path file) {
        if (!Files.exists(file)) {
            remote.download(URI.create(path.toString()), file, path);
            System.out.printf("downloading %s\n", path);
        }
    }

    private void check(Path local, Resource resource) {
        var path = resource.path();
        check(path, local.resolve(path));
        var sha1 = resource.sha1();
        check(sha1, local.resolve(sha1));
    }

    public Maven check() throws InterruptedException {
        System.out.println("check");
        for (Group group : local.repository()) for (Artifact artifact : group) check(local.local(), artifact.metadata());
        remote.get();
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }
}
