package dev.client.tenacity.utils.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CloudUtils {

    private static final String API_KEY = "id4uwJLq";
    private static final String PRODUCT_CODE = "K3r30n8A";

    public static JsonObject request(String url, Map<String, String> get, Map<String, String> post) throws IOException {
        url += (get.isEmpty() ? "" : "?") + get.entrySet().stream().map((e) -> {
            try {
                return URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8.toString()) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            return null;
        }).collect(Collectors.joining("&"));

        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://intent.store/api/configuration/" + url).openConnection();
        conn.setRequestProperty("User-Agent", "Intent-API/1.0 TenacityBot");
        conn.setRequestProperty("X-Cedobypass1337", "true");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : post.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        conn.setFixedLengthStreamingMode(length);
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.connect();
        try (OutputStream os = conn.getOutputStream()) {
            os.write(out);
        }

        InputStream stream = conn.getResponseCode() / 100 == 2 ? conn.getInputStream() : conn.getErrorStream();
        if(stream == null) return null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        JsonElement element;
        try {
            element = JsonParser.parseReader(reader);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        if (element.isJsonObject())
            return element.getAsJsonObject();

        return null;
    }


    public static JsonArray listAllData() {
        HashMap<String, String> get = createGetMap();
        HashMap<String, String> post = new HashMap<>();
        try {
            JsonObject response = request("list", get, post);
            if (response != null && response.has("response") && response.get("response").getAsString().equals("success")) {
                return response.get("configurations").getAsJsonArray();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject getData(String shareCode) {
        HashMap<String, String> get = createGetMap();
        HashMap<String, String> post = new HashMap<>();

        get.put("share_code", shareCode);

        try {
            JsonObject response = request("get", get, post);
            if (response != null && response.has("response") && response.get("response").getAsString().equals("success")) {
                return response.get("configuration").getAsJsonObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static HashMap<String, String> createGetMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", API_KEY);
        map.put("product_code", PRODUCT_CODE);
        return map;
    }

}
