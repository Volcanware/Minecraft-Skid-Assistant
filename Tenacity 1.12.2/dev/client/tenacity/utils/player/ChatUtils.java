package dev.client.tenacity.utils.player;

import dev.utils.Utils;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ChatUtils implements Utils {

    public static void print(boolean prefix, String message) {
        if (mc.player != null) {
            if (prefix) message = "§7[§d§lTENACITY§r§7] " + message;
            mc.player.addChatMessage(new TextComponentString(message));
        }
    }

    public static void print(String prefix, TextFormatting color, String message) {
        if (mc.player != null) {
            message = "§7[§" + color.formattingCode + "§l" + prefix.toUpperCase() + "§r§7]§r §" + color.formattingCode + message;
            mc.player.addChatMessage(new TextComponentString(message));
        }
    }

    public static void print(Object o) {
        print(true, String.valueOf(o));
    }

    public static void send(String message) {
        if (mc.player != null) {
            mc.player.sendChatMessage(message);
        }
    }

}
