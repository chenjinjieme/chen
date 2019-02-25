package com.chen.core.security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

public class MessageDigest {
    private java.security.MessageDigest messageDigest;

    private MessageDigest(java.security.MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }

    public static MessageDigest md5() throws NoSuchAlgorithmException {
        return new MessageDigest(java.security.MessageDigest.getInstance("MD5"));
    }

    public static MessageDigest sha1() throws NoSuchAlgorithmException {
        return new MessageDigest(java.security.MessageDigest.getInstance("SHA-1"));
    }

    public static MessageDigest sha256() throws NoSuchAlgorithmException {
        return new MessageDigest(java.security.MessageDigest.getInstance("SHA-256"));
    }

    public void update(byte input) {
        messageDigest.update(input);
    }

    public void update(byte[] input, int offset, int len) {
        messageDigest.update(input, offset, len);
    }

    public void update(byte[] input) {
        messageDigest.update(input);
    }

    public void update(ByteBuffer input) {
        messageDigest.update(input);
    }

    public byte[] digest() {
        return messageDigest.digest();
    }

    public int digest(byte[] buf, int offset, int len) throws DigestException {
        return messageDigest.digest(buf, offset, len);
    }

    public byte[] digest(byte[] input) {
        return messageDigest.digest(input);
    }

    public byte[] digest(Path path) throws IOException {
        try (var channel = FileChannel.open(path)) {
            var buffer = ByteBuffer.allocate(1024);
            for (long l = channel.size(), read; l > 0 && (read = channel.read(buffer.clear())) > 0; l -= read) messageDigest.update(buffer);
            return messageDigest.digest();
        }
    }
}
