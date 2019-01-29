package com.chen.net.http;

import com.chen.lang.ByteSequence;
import com.chen.net.URL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.LinkedHashMap;
import java.util.Map;

public class Request {
    private static final ByteSequence CRLF = new ByteSequence("\r\n".getBytes());
    private ByteBuffer buffer = ByteBuffer.allocate(8192);
    private ByteSequence sequence = new ByteSequence(buffer.array());
    private int flag;
    private String method;
    private URL url;
    private Map<String, String> headers = new LinkedHashMap<>();
    private int length = -1;

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public String getMethod() {
        return method;
    }

    public URL getURL() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int set(ByteChannel channel) throws IOException {
        return channel.read(buffer);
    }

    public boolean check() {
        boolean b = check0();
        if (b) buffer.flip();
        return b;
    }

    private boolean check0() {
        switch (flag) {
            case 0:
                if (!checkRequestLine()) return false;
            case 1:
                if (!checkHeader()) return false;
            case 2:
                if (!checkRequestBody()) return false;
        }
        return true;
    }

    private boolean checkRequestLine() {
        int l = sequence.indexOf(CRLF);
        if (l == -1) return false;
        int x = sequence.indexOf((byte) ' ');
        method = sequence.subSequence(0, x++).toString();
        int y = sequence.indexOf((byte) ' ', x);
        url = new URL(sequence.subSequence(x, y).toString());
        flag = 1;
        sequence = sequence.subSequence(l + 2);
        return true;
    }

    private boolean checkHeader() {
        int l;
        for (; (l = sequence.indexOf(CRLF)) > 0; ) {
            int x = sequence.indexOf((byte) ':');
            headers.put(sequence.subSequence(0, x).toString(), sequence.subSequence(x + 2, l).toString());
            sequence = sequence.subSequence(l + 2);
        }
        if (l == -1) return false;
        flag = 2;
        sequence = sequence.subSequence(2);
        return true;
    }

    private boolean checkRequestBody() {
        if ("POST".equals(method) && !(sequence.offset() + (length == -1 ? (length = Integer.parseInt(headers.get("Content-Length"))) : length) == buffer.limit()))
            return false;
        flag = 3;
        return true;
    }
}
