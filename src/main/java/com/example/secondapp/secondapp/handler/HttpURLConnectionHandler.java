package com.example.secondapp.secondapp.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class HttpURLConnectionHandler {

    private HttpURLConnection createHttpURLConnection(String urlString, String method, boolean doOutput) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoOutput(doOutput);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return httpURLConnection;
    }

    public HttpURLConnection makeRequestWithoutData(String urlString, String method, boolean doOutput) throws IOException {
        HttpURLConnection httpURLConnection = createHttpURLConnection(urlString, method, doOutput);
        if (httpURLConnection != null) httpURLConnection.getResponseCode();

        return httpURLConnection;
    }

    public HttpURLConnection makeRequestWithData(String urlString, String method, boolean doOutput, String dataInJson) throws IOException {

        HttpURLConnection httpURLConnection = createHttpURLConnection(urlString, method, doOutput);
        if (httpURLConnection != null) {
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.connect();

            byte[] out = dataInJson.getBytes(StandardCharsets.UTF_8);

            try (OutputStream os = httpURLConnection.getOutputStream()) {
                os.write(out);
            }
        }

        return httpURLConnection;
    }

    public String readResponse(HttpURLConnection httpURLConnection) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
