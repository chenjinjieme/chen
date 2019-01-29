package com.chen.net;

public class URL {
    private String protocol;
    private String host;
    private int port = -1;
    private String path;
    private String query;

    public URL(String url) {
        int x = url.indexOf("://");
        if (x > 0) {
            protocol = url.substring(0, x);
            url = url.substring(x + 3);
        }
        x = url.indexOf("/");
        if (x == 0) path = url;
        else {
            if (x > 0) {
                path = url.substring(x);
                url = url.substring(0, x);
            }
            x = url.indexOf(":");
            if (x > 0) {
                host = url.substring(0, x);
                port = Integer.parseInt(url.substring(x + 1));
            } else host = url;
        }
        if (port == -1) port = "https".equals(protocol) ? 443 : 80;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void update(String url) {
        int x = url.indexOf(":");
        if (x > 0) {
            host = url.substring(0, x);
            port = Integer.parseInt(url.substring(x));
        } else host = url;
    }
}
