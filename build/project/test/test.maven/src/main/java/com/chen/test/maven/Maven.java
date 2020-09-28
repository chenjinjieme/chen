package com.chen.test.maven;

import java.net.URI;

public class Maven {
    private final Local local;
    private final Remote remote;

    public Maven(Local local, Remote remote) {
        this.local = local;
        this.remote = remote;
    }

    public Maven check() throws InterruptedException {
        System.out.println("check");
        var localPath = local.local();
        local.repository().groupMap().values().stream().flatMap(group -> group.artifactMap().values().stream()).forEach(artifact -> {
            var metadata = artifact.metadata();
            if (!metadata.resource()) {
                var metadataPath = metadata.path();
                remote.download(URI.create(metadataPath.toString().replace('\\', '/')), localPath, metadataPath);
                metadata.resource(true);
                System.out.printf("downloading %s\n", metadataPath);
            } else if (!metadata.sha1()) {
                var sha1Path = metadata.sha1Path();
                remote.download(URI.create(sha1Path.toString().replace('\\', '/')), localPath, sha1Path);
                metadata.sha1(true);
                System.out.printf("downloading %s\n", sha1Path);
            }
        });
        remote.get();
        System.out.println("----------------------------------------------------------------------------------------------------");
        return this;
    }
}
