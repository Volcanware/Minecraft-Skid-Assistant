package dev.client.tenacity.ui.altmanager.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.client.tenacity.Tenacity;
import dev.client.tenacity.utils.misc.NetworkingUtils;

import java.io.*;

public class KingGenApi {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
    private final File kingAltData = new File(Tenacity.DIRECTORY, "KingGen.json");
    public String generated = "0";
    public String generatedToday = "0";
    public String username = "";
    private String key = "";

    public void setKey(String key) {
        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("key", key);
        try {
            Writer writer = new BufferedWriter(new FileWriter(kingAltData));
            gson.toJson(keyObject, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.key = key;
    }

    public void refreshKey() {
        JsonObject fileContent;
        try {
            fileContent = JsonParser.parseReader(new FileReader(kingAltData)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (fileContent.has("key")) {
            this.key = fileContent.get("key").getAsString();
        }

        NetworkingUtils.HttpResponse request = NetworkingUtils.httpsConnection("https://kinggen.wtf/api/v2/profile?key=" + this.key);
        if (request == null) return;
        if (request.response() != 200) return;
        JsonObject responseObject = JsonParser.parseString(request.content()).getAsJsonObject();
        if (responseObject.has("username")) {
            this.username = responseObject.get("username").getAsString();
        }
        if (responseObject.has("generated")) {
            this.generated = responseObject.get("generated").getAsString();
            this.generatedToday = responseObject.get("generatedToday").getAsString();
        }
    }

    public boolean checkKey() {
        if (this.key.equals("")) {
            return false;
        }
        NetworkingUtils.HttpResponse request = NetworkingUtils.httpsConnection("https://kinggen.wtf/api/v2/profile?key=" + this.key);
        if (request == null) return false;
        if (request.response() != 200) return false;
        JsonObject responseObject = JsonParser.parseString(request.content()).getAsJsonObject();
        if (responseObject.has("username")) {
            this.username = responseObject.get("username").getAsString();
        }
        if (responseObject.has("generated")) {
            this.generated = responseObject.get("generated").getAsString();
            this.generatedToday = responseObject.get("generatedToday").getAsString();
        }
        return true;
    }

    public boolean hasKeyInFile() {
        JsonObject fileContent;
        try {
            fileContent = JsonParser.parseReader(new FileReader(kingAltData)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            kingAltData.mkdir();
            e.printStackTrace();
            return false;
        }
        return fileContent.has("key") && !fileContent.get("key").getAsString().equals("");
    }

    public final String[] genAlt() {
        String[] errorResponse = {"error", "error"};
        if (key.isEmpty()) {
            try {
                Reader reader = new FileReader(kingAltData);
                JsonObject fileContent = JsonParser.parseReader(reader).getAsJsonObject();
                if (fileContent.has("key")) key = fileContent.get("key").getAsString();
                else return errorResponse;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        NetworkingUtils.HttpResponse request = NetworkingUtils.httpsConnection("https://kinggen.wtf/api/v2/alt?key=" + this.key);
        if (request == null) return errorResponse;
        if (request.response() != 200) return errorResponse;
        JsonObject responseObject = JsonParser.parseString(request.content()).getAsJsonObject();
        if (responseObject.has("email") && responseObject.has("password"))
            return new String[]{responseObject.get("email").getAsString(), responseObject.get("password").getAsString()};
        return errorResponse;
    }
}