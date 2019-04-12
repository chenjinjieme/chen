package com.chen.core.net;

public class URL {
    private String protocol;
    private String host;
    private int port = -1;
    private String path = "/";
    private String query;

    public URL(String url) {
        var start = 0;
        var x = url.indexOf("://");
        if (x > 0) {
            protocol = url.substring(0, x);
            start = x + 3;
        }
        x = url.indexOf("/", start);
        if (x == 0) path = url;
        else {
            var end = url.length();
            if (x > 0) {
                path = url.substring(x);
                end = x;
            }
            x = url.indexOf(":", start);
            if (x > 0) {
                host = url.substring(start, x);
                port = Integer.parseInt(url.substring(x + 1, end));
            } else host = url.substring(start, end);
        }
        if (port == -1) port = "https".equals(protocol) ? 443 : 80;
    }

    public String protocol() {
        return protocol;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String authority() {
        return host + ":" + port;
    }

    public String path() {
        return path;
    }

    public void update(String url) {
        var x = url.indexOf(":");
        if (x > 0) {
            host = url.substring(0, x);
            port = Integer.parseInt(url.substring(x));
        } else host = url;
    }
}
