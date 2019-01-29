package com.chen.nhentai;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class Download {
    private static final String PATH = "/Users/admin/Downloads";
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(100);
        URI uri;
        try {
            uri = new URI(new Scanner(System.in).next());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String host = uri.getScheme() + "://" + uri.getHost();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build(); InputStream inputStream = new BufferedInputStream(httpClient.execute(new HttpGet(uri)).getEntity().getContent())) {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(inputStream));
            Document document = parser.getDocument();
            File file = new File(PATH, document.getElementById("info").getElementsByTagName("h2").item(0).getTextContent());
            if (!file.exists() && !file.mkdirs()) throw new RuntimeException();
            NodeList a = document.getElementById("thumbnail-container").getElementsByTagName("a");
            int l = a.getLength();
            Thread[] threads = new Thread[l];
            for (int i = 0; i < l; i++) {
                int x = i;
                threads[i] = new Thread(() -> {
                    try {
                        CloseableHttpResponse response;
                        synchronized (Download.class) {
                            response = httpClient.execute(new HttpGet(host + a.item(x).getAttributes().getNamedItem("href").getTextContent()));
                        }
                        try (InputStream inputStream1 = new BufferedInputStream(response.getEntity().getContent())) {
                            parser.parse(new InputSource(inputStream1));
                            String url = parser.getDocument().getElementById("image-container").getElementsByTagName("img").item(0).getAttributes().getNamedItem("src").getTextContent();
                            String name = url.substring(url.lastIndexOf("/") + 1);
                            HttpEntity entity = httpClient.execute(new HttpGet(url)).getEntity();
                            try (InputStream inputStream2 = new BufferedInputStream(entity.getContent()); RandomAccessFile accessFile = new RandomAccessFile(new File(file, name), "rw")) {
                                int length = (int) entity.getContentLength();
                                accessFile.setLength(length);
                                MappedByteBuffer buffer = null;
                                try (FileChannel channel = accessFile.getChannel()) {
                                    buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, length);
                                    int bufferSize, readSize;
                                    for (byte[] bytes = new byte[bufferSize = Math.min(BUFFER_SIZE, length)]; (readSize = inputStream2.read(bytes, 0, Math.min(bufferSize, length))) > 0; length -= readSize)
                                        buffer.put(bytes, 0, readSize);
//                                } finally {
//                                    if (buffer != null) ((DirectBuffer) buffer).cleaner().clean();
                                }
                            }
                        }
                    } catch (SAXException | IOException e) {
                        e.printStackTrace();
                    }
                });
                threads[i].start();
            }
            for (Thread thread : threads) thread.join();
        } catch (IOException | SAXException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
