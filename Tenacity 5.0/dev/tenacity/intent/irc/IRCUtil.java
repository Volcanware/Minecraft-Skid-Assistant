package dev.tenacity.intent.irc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tenacity.intent.cloud.Cloud;
import dev.tenacity.module.impl.render.IRC;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.multiplayer.ServerData;
import store.intent.irc.client.IntentIRC;
import store.intent.irc.client.authorization.SessionProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class IRCUtil implements Utils {

    public static IntentIRC INSTANCE;

    public static Map<String, String> usersMap = new HashMap<>();


    public void connect(String apiKey) {
        IntentIRC irc = IntentIRC.draft(new SessionProvider(apiKey, Cloud.getProductCode())
                ).at("142.44.157.222", 8564).reconnect()
                .addFailListener((e) -> NotificationManager.post(NotificationType.WARNING, "IRC Error", "Failed to connect to the server"))
                .addLostListener((e) -> NotificationManager.post(NotificationType.WARNING, "IRC Error", "Connection lost"))

                .addAuthorizeListener((instance) -> {
                    NotificationManager.post(NotificationType.SUCCESS, "IRC Connected", "You are successfully connected to IRC");

                    INSTANCE = instance;

                    instance.setIGN("");
                }).addMessageListener((type, message) -> {
                    if (mc.thePlayer != null && mc.theWorld != null) {
                        switch (type) {
                            case "chat":
                                if (GuiChat.ircSettings.getSelected().equals("Tenacity")) {
                                    ChatUtil.irc(message);
                                }
                                break;
                            case "all_users_map":
                                JsonElement userJsonMap = JsonParser.parseString(message);
                                if (userJsonMap.isJsonObject()) {
                                    JsonObject obj = userJsonMap.getAsJsonObject();
                                    Map<String, String> usersMap = new HashMap<>();
                                    for (Entry<String, JsonElement> user : obj.entrySet()) {
                                        String ign = user.getKey();
                                        String username = user.getValue().getAsString();

                                        usersMap.put(ign, username);
                                    }
                                    IRCUtil.usersMap = usersMap;
                                }
                                break;
                            case "intent_chat":
                                if (GuiChat.ircSettings.getSelected().equals("Intent")) {
                                    ChatUtil.irc(message);
                                }
                                break;
                            case "intent_chat_ign_mapped":
                                JsonElement intentJson = JsonParser.parseString(message);
                                if (intentJson.isJsonObject()) {
                                    JsonObject obj = intentJson.getAsJsonObject();
                                    if (obj.has("sender") && obj.has("msg"))
                                        onPlayerExclaim(obj.get("sender").getAsString(), obj.get("msg").getAsString(), true);
                                }
                                break;
                            case "chat_ign_mapped":
                                JsonElement chatJson = JsonParser.parseString(message);
                                if (chatJson.isJsonObject()) {
                                    JsonObject obj = chatJson.getAsJsonObject();
                                    if (obj.has("sender") && obj.has("msg"))
                                        onPlayerExclaim(obj.get("sender").getAsString(), obj.get("msg").getAsString(), false);
                                }
                                break;
                        }
                    }
                });

        new Thread(() -> {
            if (!irc.connect()) return;
        }).start();
    }

    public void onPlayerExclaim(String ign, String message, boolean global) {

    }

    public void onMessage(String message) {
        if (INSTANCE == null) return;

        switch (GuiChat.ircSettings.getSelected()){
            case "Intent":
                INSTANCE.sendMessage("intent_chat", message);
                break;
            case "Tenacity":
                INSTANCE.sendMessage("chat", message);
                break;
        }
    }


    public void onTick() {
        onUsernameChange();
        onServerChange();
    }

    public void onServerChange() {
        if (INSTANCE == null) return;

        ServerData data = mc.getCurrentServerData();

        if (data != null) {
            INSTANCE.setServer(data.serverIP);
        } else {
            INSTANCE.setServer("");
        }
    }

    public void onUsernameChange() {
        if (INSTANCE == null) return;

        if (IRC.shareUsername.isEnabled()) {
            INSTANCE.setIGN(mc.getSession().getUsername());
        } else {
            INSTANCE.setIGN("");
        }

    }

    public static void onShutdown() {
        INSTANCE.disconnect();
        INSTANCE = null;
        usersMap = new HashMap<>();
    }

}
