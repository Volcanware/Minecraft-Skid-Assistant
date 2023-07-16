package dev.utils.misc;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.server.SPacketResourcePackSend;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Enhancements {

    /**
     * Patcher: Resource Exploit Fix (https://github.com/Sk1erLLC/Patcher/blob/master/src/main/java/club/sk1er/patcher/hooks/NetHandlerPlayClientHook.java)
     */
    public static boolean validateResourcePackUrl(NetHandlerPlayClient client, SPacketResourcePackSend packet) {
        try {
            String url = packet.getURL();
            final URI uri = new URI(url);
            final String scheme = uri.getScheme();
            final boolean isLevelProtocol = scheme.equals("level");

            if (!scheme.equals("http") && !scheme.equals("https") && !isLevelProtocol) {
                //client.getNetworkManager().sendPacket(new CPacketResourcePackStatus(packet.getHash(), CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                throw new URISyntaxException(url, "Wrong protocol");
            }

            url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());

            if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }

            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

}
