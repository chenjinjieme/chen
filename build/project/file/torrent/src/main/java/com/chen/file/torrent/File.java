package com.chen.file.torrent;

import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Integer;
import com.chen.core.bencode.List;
import com.chen.core.bencode.Value;
import com.chen.core.util.WrappedMap;

import java.nio.file.Path;
import java.util.Map;

import static com.chen.file.torrent.Info.LENGTH;
import static com.chen.file.torrent.Info.PATH;

public class File extends WrappedMap<ByteString, Value> {
    File(Map<ByteString, Value> map) {
        super(map);
    }

    public Path path() {
        var path = Path.of("");
        for (var value : ((List) get(PATH))) path = path.resolve(value.toString());
        return path;
    }

    public long length() {
        return ((Integer) get(LENGTH)).value();
    }
}
