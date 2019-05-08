package com.chen.api.httpClient;

import com.chen.core.io.ByteBufferOutputStream;
import com.chen.core.math.HexUtil;
import com.chen.core.nio.channels.BufferedReadableByteChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponseChannel extends BufferedReadableByteChannel {
    private ByteBufferOutputStream stream;
    private int statusCode;
    private String reasonPhrase;
    private Map<String, List<String>> headers = new HashMap<>();
    private boolean chunked;
    private long contentLength;

    HttpResponseChannel(ReadableByteChannel channel, ByteBufferOutputStream stream) throws IOException {
        super(channel, 16916);
        this.stream = stream;
        readStatus();
        readHeaders();
        if (chunked) readChunk();
    }

    public int statusCode() {
        return statusCode;
    }

    public String reasonPhrase() {
        return reasonPhrase;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public boolean isChunked() {
        return chunked;
    }

    public long contentLength() {
        return chunked ? -1 : contentLength;
    }

    private void readLine() throws IOException {
        var b = super.read();
        for (; b != '\r'; b = super.read()) stream.write(b);
        if ((b = super.read()) != '\n') {
            stream.write('\r');
            stream.write(b);
            readLine();
        }
    }

    private void readStatus() throws IOException {
        stream.clear();
        readLine();
        var sequence = stream.toByteSequence();
        var x = sequence.indexOf((byte) ' ') + 1;
        var y = sequence.indexOf((byte) ' ', x);
        statusCode = Integer.parseInt(sequence.subSequence(x, y).toString());
        reasonPhrase = sequence.subSequence(y + 1).toString();
    }

    private void readHeaders() throws IOException {
        for (stream.clear(), readLine(); stream.size() > 0; stream.clear(), readLine()) {
            var sequence = stream.toByteSequence();
            var x = sequence.indexOf((byte) ':');
            headers.computeIfAbsent(sequence.subSequence(0, x).toString(), k -> new ArrayList<>()).add(sequence.subSequence(x + 2).toString());
        }
        var list = headers.get("Content-Length");
        if (list == null) chunked = true;
        else contentLength = Long.parseLong(list.get(0));
    }

    private void readChunk() throws IOException {
        stream.clear();
        readLine();
        for (var b : stream) contentLength = contentLength << 4 | HexUtil.DECIMAL[b];
        if (contentLength == 0) {
            readLine();
            close();
        }
    }

    private void check() throws IOException {
        if (contentLength == 0) if (chunked) {
            readLine();
            readChunk();
        } else close();
    }

    public byte read() throws IOException {
        var b = super.read();
        contentLength--;
        check();
        return b;
    }

    public int read(ByteBuffer dst) throws IOException {
        if (contentLength < dst.remaining()) return read(dst, (int) contentLength);
        var read = super.read(dst);
        contentLength -= read;
        check();
        return read;
    }
}
