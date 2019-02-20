package com.chen.file.torrent;

import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Dictionary;

public class Info extends Dictionary {
    private static final ByteString FILES = new ByteString("files"), LENGTH = new ByteString("length"), PATH = new ByteString("path"), NAME = new ByteString("name"), PIECE_LENGTH = new ByteString("piece length"), PIECES = new ByteString("pieces");

    Info(Torrent torrent) {
        super((Dictionary) torrent.get(Torrent.INFO));
    }

    public boolean multiple() {
        return get(FILES) != null;
    }
}
