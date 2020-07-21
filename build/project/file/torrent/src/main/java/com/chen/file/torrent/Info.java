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
    private final Dictionary dictionary;

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
        return Optional.ofNullable((List) dictionary.get(FILES)).map(files -> files.stream().map(file -> (Dictionary) file).map(file -> new File((Integer) file.get(LENGTH), (List) file.get(PATH)))).or(() -> Optional.of(Stream.of(new File((Integer) dictionary.get(LENGTH), List.of(dictionary.get(NAME)))))).get().collect(Collectors.toList());
    }

    public int pieceLength() {
        return (int) ((Integer) dictionary.get(PIECE_LENGTH)).value();
    }

    public java.util.List<Piece> pieces() {
        var buffer = ((ByteString) dictionary.get(PIECES)).buffer();
        var pieces = new ArrayList<Piece>(buffer.remaining() / 20);
        var limit = buffer.limit();
        for (var i = buffer.position(); i < limit; ) pieces.add(new Piece(buffer.duplicate().position(i).limit(i += 20)));
        return pieces;
    }
}
