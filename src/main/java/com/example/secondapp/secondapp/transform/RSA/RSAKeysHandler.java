package com.example.secondapp.secondapp.transform.RSA;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
@Slf4j
public class RSAKeysHandler {
    private static final String ALGORITHM = "RSA";

    @Value("${rsa.publicKey.path}")
    private String publicKeyPath;

    @Value("${rsa.privateKey.path}")
    private String privateKeyPath;

    public PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = readBYtesFromFile(privateKeyPath);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            return kf.generatePrivate(spec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn(e.getMessage());
        }

        return null;
    }

    public PublicKey getPublicKey() {
        try {
            byte[] keyBytes = readBYtesFromFile(publicKeyPath);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);

            return kf.generatePublic(spec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }

        return null;
    }

    private byte[] readBYtesFromFile(String path) {
        File file = new File(path);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        return new byte[0];
    }

    public PublicKey createPublicKeyOutOfBytes(byte[] publicKeyBytes) {
        try {
            return KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    public PrivateKey createPrivateKeyOutOfBytes(byte[] privateKeyBytes)  {
        try {
            return KeyFactory.getInstance(ALGORITHM).generatePrivate(new X509EncodedKeySpec(privateKeyBytes));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }
        return null;
    }


}
