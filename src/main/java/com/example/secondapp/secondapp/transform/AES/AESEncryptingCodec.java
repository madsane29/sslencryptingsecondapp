package com.example.secondapp.secondapp.transform.AES;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
@Slf4j
public class AESEncryptingCodec {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    public byte[] decrypt(byte[] data, SecretKey secretKey) {
        try {
            ByteBuffer bb = ByteBuffer.wrap(data);

            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherText = new byte[data.length - IV_LENGTH];
            bb.get(iv, 0, IV_LENGTH);
            bb.get(cipherText, 0, data.length - IV_LENGTH);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec/*new IvParameterSpec(iv)*/);

            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return new byte[0];
    }

    public byte[] encrypt(byte[] data, SecretKey secretKey) {

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.warn(e.getMessage());
        }


        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

        if (cipher != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
                log.warn(e.getMessage());
            }
            
            byte[] plainTextBytes = data;

            byte[] cipherText = new byte[0];
            try {
                cipherText = cipher.doFinal(plainTextBytes);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                log.warn(e.getMessage());
            }
            return ByteBuffer.allocate(IV_LENGTH + cipherText.length)
                    .put(iv)
                    .put(cipherText)
                    .array();
        }

        return new byte[0];

    }

}
