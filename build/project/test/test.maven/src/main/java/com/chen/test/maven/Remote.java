package com.chen.test.maven;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
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

    public Remote download(URI uri, Path path, Path name) {
        futureList.add(client.sendAsync(HttpRequest.newBuilder().uri(this.uri.resolve(uri)).build(), HttpResponse.BodyHandlers.ofFile(path)).thenAcceptAsync(response -> {
            var code = response.statusCode();
            if (code != 200) {
                try {
                    Files.delete(response.body());
                    System.out.printf("fail %s %s\n", code, name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else System.out.printf("downloaded %s\n", name);
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
