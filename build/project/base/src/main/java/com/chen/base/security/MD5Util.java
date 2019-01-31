package com.chen.base.security;

import com.chen.base.math.HexUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MD5Util {
    private static final MessageDigest MESSAGE_DIGEST;

    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized String digest(String s) {
        return HexUtil.getHex(MESSAGE_DIGEST.digest(s.getBytes()));
    }

    public static synchronized String digest(File file) throws IOException {
        try (DigestInputStream inputStream = new DigestInputStream(new BufferedInputStream(new FileInputStream(file)), MESSAGE_DIGEST)) {
            byte[] bytes = new byte[1024];
            for (long l = file.length(); l > 0; ) l -= inputStream.read(bytes);
            return HexUtil.getHex(MESSAGE_DIGEST.digest());
        }
    }
}
