package com.example.secondapp.secondapp.transform.AES;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Component
@Slf4j
public class KeyStoreHandler {
    private static final String KEY_STORE_TYPE = "JCEKS";

    private KeyStoreHandler() {
    }

    private static KeyStore loadKeyStore(final String filePath, final String password) {
        File file = new File(filePath);
        KeyStore keyStore = null;

        try {
            keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        } catch (KeyStoreException e) {
            log.warn(e.getMessage());
        }

        if (keyStore != null) {
            if (!file.exists()) {
                try {
                    keyStore.load(null, password.toCharArray());
                } catch (IOException | CertificateException | NoSuchAlgorithmException e) {
                    log.warn(e.getMessage());
                }
            } else {
                try (InputStream readStream = new FileInputStream(filePath)) {
                    keyStore.load(readStream, password.toCharArray());
                } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
                    log.warn(e.getMessage());
                }
            }
        }

        return keyStore;
    }

    public static void storeSecretKey(final SecretKey secretKey, final String alias, final String password, final String filePath, final String filePassword) {
        try {
            KeyStore keyStore = loadKeyStore(filePath, filePassword);
            if (keyStore != null) {
                keyStore.setKeyEntry(alias, secretKey, password.toCharArray(), null);
                try (OutputStream writeStream = new FileOutputStream(filePath)) {
                    keyStore.store(writeStream, password.toCharArray());
                }
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            log.warn(e.getMessage());
        }
    }

    public static SecretKey getSecretKey(final String alias, final String password, final String filePath, final String filePassword) {

        try (InputStream readStream = new FileInputStream(filePath)) {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(readStream, filePassword.toCharArray());
            return (SecretKey) keyStore.getKey(alias, password.toCharArray());
        } catch (IOException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            log.warn(e.getMessage());
        }

        return null;
    }
}
