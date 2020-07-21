package com.chen.file.torrent;

import com.chen.core.bencode.Integer;
import com.chen.core.bencode.List;

import java.nio.file.Path;

public class File {
    private final Integer length;
    private final List list;
    private Path path;

    File(Integer length, List list) {
        this.length = length;
        this.list = list;
    }

    public long length() {
        return length.value();
    }

    public Path path() {
        if (path != null) return path;
        path = Path.of("");
        for (var value : list) path = path.resolve(value.toString());
        return path;
    }
}
