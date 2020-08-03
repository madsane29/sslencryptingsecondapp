package com.example.secondapp.secondapp.transform.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

@Component
@Slf4j
public class GenerateAESKey {

    private static final int KEY_BIT_SIZE = 256;
    private static final String ALGORITHM = "AES";

    @Value("${aes.alias}")
    private String alias;

    @Value("${aes.password}")
    private String password;

    @Value("${aes.filePath}")
    private String filePath;

    @Value("${aes.filePassword}")
    private String filePassword;


    public SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_BIT_SIZE, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();

            KeyStoreHandler.storeSecretKey(secretKey, alias, password, filePath, filePassword);

            return secretKey;
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }
}
