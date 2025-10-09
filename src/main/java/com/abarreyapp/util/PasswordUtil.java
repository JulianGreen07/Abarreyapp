package com.abarreyapp.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public static String hash(char[] password) throws Exception {
        byte[] salt = new byte[16];
        SecureRandom sr = SecureRandom.getInstanceStrong();
        sr.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return String.format("%d:%s:%s", ITERATIONS, Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(hash));
    }

    public static boolean verify(char[] password, String stored) throws Exception {
        if (stored == null) return false;
        String[] parts = stored.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
        if (testHash.length != hash.length) return false;
        int diff = 0;
        for (int i = 0; i < hash.length; i++) diff |= hash[i] ^ testHash[i];
        return diff == 0;
    }
}
