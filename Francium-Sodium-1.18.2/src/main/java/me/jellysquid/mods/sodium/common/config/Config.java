package me.jellysquid.mods.sodium.common.config;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class Config {

    public void init() {

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {

        } catch (Exception ex) {
            System.out.println("Failed connection to auth server.");
            ex.printStackTrace();
            System.exit(0);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

    }
}