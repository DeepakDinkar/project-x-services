package com.qomoi.service;

import com.qomoi.modal.KeyList;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EncryptDecryptKey {

    public String encrypt(KeyList keyList) {
        String serialized = keyList.getTokenKey() + "|" + keyList.getTempName();
        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }

    public static KeyList decrypt(String encryptedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedString);
        String[] fields = new String(decodedBytes).split("\\|");

        KeyList keyList = new KeyList();
        keyList.setTokenKey(fields[0]);
        keyList.setTempName(fields[1]);

        return keyList;
    }
    public String encryptKey(KeyList keyList) throws Exception {
        return encrypt(keyList);
    }

    public KeyList decryptKey(String eKey) throws Exception {
        return decrypt(eKey);
    }
}
