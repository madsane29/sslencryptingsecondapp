package com.example.secondapp.secondapp.transform.RSA;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class RSAEncryptingCodec {

    private static final String ALGORITHM = "RSA";

    public byte[] encrypt(byte[] decrypted, Key key) {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key);
        try {
            if (cipher != null)
                return cipher.doFinal(decrypted);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.warn(e.getMessage());
        }
        return new byte[0];
    }

    public byte[] decrypt(byte[] encrypted, Key key) {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key);
        try {
            if (cipher != null)
                return cipher.doFinal(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.warn(e.getMessage());
        }


        return new byte[0];
    }

    private Cipher getCipher(int mode, Key key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, key);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn(e.getMessage());
        }
        return cipher;
    }

}