package com.nexustech.smartbrowser.security;

import android.util.Base64;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Functional crypto engine with fluent API
 */
public class CryptoEngine {
    
    private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGO = "AES";
    private static final int KEY_SIZE = 16;
    
    // Embedded secrets (no config class)
    private static final String SECRET_SEED = "tyduygu";
    private static final String IV_SEED = "tycfuyg";
    
    private final Function<String, Optional<String>> encryptor;
    private final Function<String, Optional<String>> decryptor;
    
    public CryptoEngine() {
        this.encryptor = this::performEncryption;
        this.decryptor = this::performDecryption;
    }
    
    public String encrypt(String plaintext) {
        return encryptor.apply(plaintext).orElse("");
    }
    
    public String decrypt(String ciphertext) {
        return decryptor.apply(ciphertext).orElse(null);
    }
    
    private Optional<String> performEncryption(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return Optional.of(plaintext);
        }
        
        try {
            Cipher cipher = createCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
            return Optional.of(Base64.encodeToString(encrypted, Base64.DEFAULT));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private Optional<String> performDecryption(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return Optional.ofNullable(ciphertext);
        }
        
        try {
            Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(Base64.decode(ciphertext, Base64.DEFAULT));
            return Optional.of(new String(decrypted, "UTF-8"));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private Cipher createCipher(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(deriveKey(SECRET_SEED), KEY_ALGO);
        IvParameterSpec ivSpec = new IvParameterSpec(deriveKey(IV_SEED));
        cipher.init(mode, keySpec, ivSpec);
        return cipher;
    }
    
    private byte[] deriveKey(String seed) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha.digest(seed.getBytes("UTF-8"));
            return Arrays.copyOf(hash, KEY_SIZE);
        } catch (Exception e) {
            return new byte[KEY_SIZE];
        }
    }
}
