package com.example.secondapp.secondapp.service;

import com.example.secondapp.secondapp.exception.ResourceNotFoundException;
import com.example.secondapp.secondapp.api.MyService;
import com.example.secondapp.secondapp.dto.DataAndKeysDTO;
import com.example.secondapp.secondapp.dto.KeyDTO;
import com.example.secondapp.secondapp.dto.UserDTO;
import com.example.secondapp.secondapp.handler.HttpURLConnectionHandler;
import com.example.secondapp.secondapp.transform.AES.AESEncryptingCodec;
import com.example.secondapp.secondapp.transform.AES.GenerateAESKey;
import com.example.secondapp.secondapp.transform.RSA.RSAEncryptingCodec;
import com.example.secondapp.secondapp.transform.RSA.RSAKeysHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyServiceImpl implements MyService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with this id :: ";

    private static final String URL = "https://localhost:8443/api";

    private final HttpURLConnectionHandler httpURLConnectionHandler;
    private final GenerateAESKey generateAESKey;
    private final AESEncryptingCodec aesEncryptingCodec;
    private final RSAEncryptingCodec rsaEncryptingCodec;
    private final RSAKeysHandler rsaKeysHandler;
    private final Gson gson;


    @Override
    public UserDTO add(UserDTO userDTO) throws IOException {
        UUID uuid = UUID.nameUUIDFromBytes(userDTO.toString().getBytes());
        userDTO.setUuid(uuid);

        String userDTOJson = gson.toJson(userDTO);

        DataAndKeysDTO dataAndKeysDTO = encrypt(userDTOJson.getBytes());
        String dataAndKeysDTOInJson = gson.toJson(dataAndKeysDTO);

        HttpURLConnection httpURLConnection = httpURLConnectionHandler
                .makeRequestWithData(URL, "POST", true, dataAndKeysDTOInJson);
        String encryptedResponse = httpURLConnectionHandler.readResponse(httpURLConnection);

        String decryptedDataInJson = decrypt(gson.fromJson(encryptedResponse, DataAndKeysDTO.class));

        return gson.fromJson(decryptedDataInJson, UserDTO.class);
    }

    @Override
    public List<UserDTO> list() throws IOException {
        String publicKey = Base64.encodeBase64String(rsaKeysHandler.getPublicKey().getEncoded());
        KeyDTO keyDTO = new KeyDTO(publicKey);
        String keyInJson = gson.toJson(keyDTO);

        HttpURLConnection httpURLConnection = httpURLConnectionHandler
                .makeRequestWithData(URL + "/list", "POST", true, keyInJson);
        String encryptedResponse = httpURLConnectionHandler.readResponse(httpURLConnection);

        String decryptedDataInJson = decrypt(gson.fromJson(encryptedResponse, DataAndKeysDTO.class));

        Type userListType = new TypeToken<ArrayList<UserDTO>>() {
        }.getType();
        return gson.fromJson(decryptedDataInJson, userListType);
    }

    @Override
    public UserDTO get(long id) throws Exception {
        String publicKey = Base64.encodeBase64String(rsaKeysHandler.getPublicKey().getEncoded());
        KeyDTO keyDTO = new KeyDTO(publicKey);
        String keyInJson = gson.toJson(keyDTO);
        HttpURLConnection httpURLConnection = httpURLConnectionHandler.makeRequestWithData(URL + "/" + id, "POST", true, keyInJson);
        String encryptedResponse = httpURLConnectionHandler.readResponse(httpURLConnection);
        if (encryptedResponse != null) {
            String decryptedDataInJson = decrypt(gson.fromJson(encryptedResponse, DataAndKeysDTO.class));

            return gson.fromJson(decryptedDataInJson, UserDTO.class);
        }

        throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + id);
    }

    @Override
    public UserDTO update(long id, UserDTO userDTO) throws IOException {
        String userDTOJson = gson.toJson(userDTO);
        DataAndKeysDTO dataAndKeysDTO = encrypt(userDTOJson.getBytes());
        String dataAndKeysDTOInJson = gson.toJson(dataAndKeysDTO);
        HttpURLConnection httpURLConnection = httpURLConnectionHandler
                .makeRequestWithData(URL + "/" + id, "PUT", true, dataAndKeysDTOInJson);
        String encryptedResponse = httpURLConnectionHandler.readResponse(httpURLConnection);
        String decryptedDataInJson = decrypt(gson.fromJson(encryptedResponse, DataAndKeysDTO.class));


        return gson.fromJson(decryptedDataInJson, UserDTO.class);
    }

    @Override
    public void delete(long id) throws IOException {
        httpURLConnectionHandler.makeRequestWithoutData(URL + "/" + id, "DELETE", false);
    }

    private DataAndKeysDTO encrypt(byte[] data) throws IOException {
        PublicKey publicKey = rsaKeysHandler.createPublicKeyOutOfBytes(getPublicKey(URL).getEncoded());

        SecretKey secretKey = generateAESKey.generateSecretKey();

        byte[] encryptedData = aesEncryptingCodec.encrypt(data, secretKey);

        byte[] encryptedAESKey = rsaEncryptingCodec.encrypt(secretKey.getEncoded(), publicKey);
        return new DataAndKeysDTO(Base64.encodeBase64String(encryptedData), Base64.encodeBase64String(encryptedAESKey), Base64.encodeBase64String(rsaKeysHandler.getPublicKey().getEncoded()));
    }

    private PublicKey getPublicKey(String url) throws IOException {
        HttpURLConnection httpURLConnection = httpURLConnectionHandler.makeRequestWithoutData(url + "/get_public_key", "GET", false);

        String response = httpURLConnectionHandler.readResponse(httpURLConnection);
        KeyDTO keyDTO = gson.fromJson(response, KeyDTO.class);
        byte[] keyBytes = Base64.decodeBase64(keyDTO.getKey());
        return rsaKeysHandler.createPublicKeyOutOfBytes(keyBytes);
    }

    private String decrypt(DataAndKeysDTO dataAndKeysDTO) {
        String data = dataAndKeysDTO.getData();
        String key = dataAndKeysDTO.getKeyUsedForEncrypting();

        byte[] dataBytes = Base64.decodeBase64(data);
        byte[] keyBytes = Base64.decodeBase64(key);

        byte[] decryptedKeyBytes = rsaEncryptingCodec.decrypt(keyBytes, rsaKeysHandler.getPrivateKey());
        SecretKey secretKeyGotDecrypted = new SecretKeySpec(decryptedKeyBytes, 0, decryptedKeyBytes.length, "AES");

        byte[] decryptedData = aesEncryptingCodec.decrypt(dataBytes, secretKeyGotDecrypted);

        return new String(decryptedData, StandardCharsets.UTF_8);
    }

}
