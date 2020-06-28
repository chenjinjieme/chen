package com.chen.file.torrent;

import com.chen.core.bencode.Integer;
import com.chen.core.bencode.List;

import java.nio.file.Path;

public class File {
    private Integer length;
    private List path;

    File(Integer length, List path) {
        this.length = length;
        this.path = path;
    }

    public long length() {
        return length.value();
    }

    public Path path() {
        var path = Path.of("");
        for (var value : this.path) path = path.resolve(value.toString());
        return path;
    }
}
