package com.chen.file.torrent;

import com.chen.core.bencode.ByteString;
import com.chen.core.bencode.Dictionary;
import com.chen.core.bencode.Integer;
import com.chen.core.bencode.List;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Info {
    public static final ByteString FILES = new ByteString("files");
    public static final ByteString LENGTH = new ByteString("length");
    public static final ByteString PATH = new ByteString("path");
    public static final ByteString NAME = new ByteString("name");
    public static final ByteString PIECE_LENGTH = new ByteString("piece length");
    public static final ByteString PIECES = new ByteString("pieces");
    private Dictionary dictionary;

    Info(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public boolean multiple() {
        return dictionary.get(FILES) != null;
    }

    public String name() {
        return dictionary.get(NAME).toString();
    }

    public java.util.List<File> files() {
        return Optional.ofNullable((List) dictionary.get(FILES)).map(files -> files.stream().map(file -> (Dictionary) file).map(file -> new File(((Integer) file.get(LENGTH)), ((List) file.get(PATH))))).or(() -> Optional.of(Stream.of(new File((Integer) dictionary.get(LENGTH), List.of(dictionary.get(NAME)))))).get().collect(Collectors.toList());
    }

    public int pieceLength() {
        return (int) ((Integer) dictionary.get(PIECE_LENGTH)).value();
    }

    public java.util.List<Piece> pieces() {
        var buffer = ((ByteString) dictionary.get(PIECES)).buffer();
        var pieceLength = pieceLength();
        var pieces = new ArrayList<Piece>(buffer.remaining() / pieceLength + 1);
        var files = files();
        var n = buffer.position();
        var offset = 0L;
        for (int i = 0, l = files.size(), index = 0; i < l; ) {
            var paths = new ArrayList<File>();
            pieces.add(new Piece(index++, buffer.duplicate().position(n).limit(n += 20), paths, offset));
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
