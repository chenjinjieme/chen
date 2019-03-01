package com.chen.file.torrent;

import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Dictionary;
import com.chen.core.bencode.Integer;
import com.chen.core.bencode.List;

import java.util.ArrayList;
import java.util.Map;

public class Info extends Dictionary {
    public static final ByteString FILES = new ByteString("files");
    public static final ByteString LENGTH = new ByteString("length");
    public static final ByteString PATH = new ByteString("path");
    public static final ByteString NAME = new ByteString("name");
    public static final ByteString PIECE_LENGTH = new ByteString("piece length");
    public static final ByteString PIECES = new ByteString("pieces");

    Info(Dictionary dictionary) {
        super(dictionary);
    }

    public String name() {
        return get(NAME).toString();
    }

    public java.util.List<File> files() {
        var files = ((List) get(FILES));
        if (files == null) return java.util.List.of(new File(Map.of(LENGTH, get(LENGTH), PATH, new List(java.util.List.of()))));
        else {
            var list = new ArrayList<File>(files.size());
            for (var file : files) list.add(new File((Dictionary) file));
            return list;
        }
    }

    public int pieceLength() {
        return (int) ((Integer) get(PIECE_LENGTH)).value();
    }

    public java.util.List<Piece> pieces() {
        var sequence = ((ByteString) get(PIECES)).sequence();
        var pieceLength = pieceLength();
        var pieces = new ArrayList<Piece>(sequence.length() / pieceLength + 1);
        var files = files();
        var n = 0;
        var offset = 0L;
        for (int i = 0, l = files.size(), index = 0; i < l; ) {
            var paths = new ArrayList<File>();
            pieces.add(new Piece(index++, sequence.subSequence(n, n += 20), paths, offset));
            for (var j = pieceLength; j > 0 && i < l; ) {
                var file = files.get(i);
                var length = file.length() - offset;
                if (length > j) {
                    offset = offset + j;
                    j = 0;
                } else {
                    offset = 0;
                    j -= length;
                    i++;
                }
                paths.add(file);
            }
        }
        return pieces;
    }
}
