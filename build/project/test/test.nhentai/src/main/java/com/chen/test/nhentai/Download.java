package com.chen.test.nhentai;

import org.jsoup.helper.HttpConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Download {
    public static void main(String[] args) throws IOException {
        var url = new URL(new Scanner(System.in).next());
        var document = HttpConnection.connect(url).get();
        var path = Path.of("/Users/admin/Downloads").resolve(document.select("#info h2").get(0).text());
        Files.createDirectories(path);
        var elements = document.select("#thumbnail-container a");
        var service = Executors.newCachedThreadPool();
        var client = HttpClient.newHttpClient();
        var builder = HttpRequest.newBuilder();
        for (var element : elements)
            service.submit(() -> {
                var href = HttpConnection.connect(new URL(url, element.attr("href"))).get().select("#image-container img").get(0).attr("src");
                System.out.println(client.send(builder.uri(URI.create(href)).build(), responseInfo -> HttpResponse.BodySubscribers.ofFile(path.resolve(href.substring(href.lastIndexOf("/") + 1)))).body());
                return null;
            });
    }
}
