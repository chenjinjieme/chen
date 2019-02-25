package com.chen.core.crypto;

import com.chen.core.math.HexUtil;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    public AES(String key) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            keyGenerator.init(secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            encryptCipher = Cipher.getInstance("AES");
            decryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new AESException(e);
        }
    }

    public byte[] encrypt(byte[] bytes) {
        try {
            return encryptCipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new AESException(e);
        }
    }

    public String encrypt(String s) {
        return HexUtil.getHex(encrypt(s.getBytes()));
    }

    public byte[] decrypt(byte[] bytes) {
        try {
            return decryptCipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new AESException(e);
        }
    }

    public String decrypt(String s) {
        return new String(decrypt(HexUtil.getBytes(s)));
    }

    private static class AESException extends RuntimeException {
        AESException(Throwable cause) {
            super(cause);
        }
    }
}
