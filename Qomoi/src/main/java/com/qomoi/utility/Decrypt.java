package com.qomoi.utility;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Arrays;

import static javax.crypto.Cipher.*;


public class Decrypt {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;


    private static final String AES_ENCRYPTION_SCHEME = "AES";
    private static final String SECRET_KEY = "WQhC69td3Fe7THz7O/X+iA==";

    public Decrypt() throws Exception {
        myEncryptionKey = "randomsecretkey";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = Arrays.copyOf(myEncryptionKey.getBytes(UNICODE_FORMAT), 24);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }

    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.getEncoder().encode(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }
    public String decrypt(String encryptedPassword) {
//        String decryptedText = null;
//        try {
//            cipher.init(DECRYPT_MODE, key);
//            byte[] encryptedText = Base64.decodeBase64(encryptedString);
//            byte[] plainText = cipher.doFinal(encryptedText);
//            decryptedText = new String(plainText, UNICODE_FORMAT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return decryptedText;

        try {

            byte[] decodedKey = java.util.Base64.getDecoder().decode(SECRET_KEY);
            SecretKeySpec secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher cipher = getInstance("AES");
            cipher.init(DECRYPT_MODE, secretKey);

            byte[] decryptedPasswordBytes = cipher.doFinal(java.util.Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedPasswordBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
