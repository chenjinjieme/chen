package com.chen.test.maven;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

public class Remote {
    private final URI uri;
    private final HttpClient client;
    private final List<Future<?>> futureList = new LinkedList<>();

    public Remote(URI uri) {
        this.uri = uri;
        client = HttpClient.newHttpClient();
    }

    public Remote download(URI uri, Path local, Path path) {
        futureList.add(client.sendAsync(HttpRequest.newBuilder().uri(this.uri.resolve(uri)).build(), HttpResponse.BodyHandlers.ofFile(local.resolve(path))).thenRun(() -> {
            System.out.printf("downloaded %s\n", path);
            synchronized (futureList) {
                futureList.notify();
            }
        }));
        return this;
    }

    public Remote get() throws InterruptedException {
        for (; ; )
            synchronized (futureList) {
                futureList.removeIf(Future::isDone);
                if (futureList.size() > 0) futureList.wait();
                else break;
            }
        return this;
    }
}
