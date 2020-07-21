package com.chen.file.torrent;

import java.nio.ByteBuffer;

public class Piece {
    private final ByteBuffer hash;

    Piece(ByteBuffer hash) {
        this.hash = hash;
    }

    public ByteBuffer hash() {
        return hash;
    }
}
