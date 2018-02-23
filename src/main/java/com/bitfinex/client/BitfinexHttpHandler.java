package com.bitfinex.client;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

public class BitfinexHttpHandler {
    private String urlPath;
    private Map<String, String> additional;
    private long nonce = System.currentTimeMillis();
    private static final String ALGORITHM_HMACSHA384 = "HmacSHA384";

    public BitfinexHttpHandler(String urlPath, Map<String, String> additional) {
        this.urlPath = urlPath;
        this.additional = additional;

    }

    public String invokePrivate(String apiKey, String apiKeySecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String method = "POST";

        URL url = new URL("https://api.bitfinex.com" + urlPath);
        HttpURLConnection conn = createHttpConnection(method, url);

        String payload = createPayload(urlPath, additional);

        Base64.Encoder encoder = Base64.getEncoder();
        String payload_base64 = encoder.encodeToString(payload.getBytes());

        String payload_sha384hmac = hmacDigest(payload_base64, apiKeySecret, ALGORITHM_HMACSHA384);

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.addRequestProperty("X-BFX-APIKEY", apiKey);
        conn.addRequestProperty("X-BFX-PAYLOAD", payload_base64);
        conn.addRequestProperty("X-BFX-SIGNATURE", payload_sha384hmac);

        // read the response
        InputStream in = new BufferedInputStream(conn.getInputStream());
        return convertStreamToString(in);
    }

    private String createPayload(String urlPath, Map<String, String> additional) {
        JSONObject jo = new JSONObject();
        jo.put("request", urlPath);
        jo.put("nonce", Long.toString(getNonce()));
        additional.entrySet().forEach(entry ->
                jo.put(entry.getKey(), entry.getValue())
        );

        return jo.toString();
    }

    private HttpURLConnection createHttpConnection(String method, URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);

        conn.setDoOutput(true);
        conn.setDoInput(true);
        return conn;
    }

    public long getNonce() {
        return ++nonce;
    }

    public static String hmacDigest(String msg, String keyString, String algo) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
        Mac mac = Mac.getInstance(algo);
        mac.init(key);

        byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

        StringBuffer hash = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hash.append('0');
            }
            hash.append(hex);
        }

        return hash.toString();
    }

    private String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String invokePublic() throws IOException {
        String method = "GET";
        URL url = new URL("https://api.bitfinex.com" + urlPath);
        HttpURLConnection conn = createHttpConnection(method, url);

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        InputStream in = new BufferedInputStream(conn.getInputStream());
        return convertStreamToString(in);
    }



}
