package com.example.secondapp.secondapp.transform.RSA;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class GenerateRSAKeys {
    private static final String ALGORITHM = "RSA";

    private GenerateRSAKeys() {}

    public static void createKeys(int keyLength, String publicKeyPath, String privateKeyPath) {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }
        if (keyGen != null) {
            keyGen.initialize(keyLength);
            KeyPair pair = keyGen.generateKeyPair();

            writeToFile(publicKeyPath, pair.getPublic().getEncoded());
            writeToFile(privateKeyPath, pair.getPrivate().getEncoded());
        }
    }

    private static void writeToFile(String path, byte[] key) {
        File f = new File(path);
        f.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(key);
            fos.flush();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }


}
