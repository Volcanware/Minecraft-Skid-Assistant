package net.optifine.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

public class CapeUtils {
    private static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]+");

    public static void downloadCape(final AbstractClientPlayer player) {
        final String s = player.getNameClear();

        if (s != null && !s.isEmpty() && !s.contains("\u0000") && PATTERN_USERNAME.matcher(s).matches()) {
            final String s1 = "http://s.optifine.net/capes/" + s + ".png";
            final ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s);
            final TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            final ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);

            if (itextureobject != null && itextureobject instanceof ThreadDownloadImageData) {
                final ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) itextureobject;

                if (threaddownloadimagedata.imageFound != null) {
                    if (threaddownloadimagedata.imageFound.booleanValue()) {
                        player.setLocationOfCape(resourcelocation);

                        if (threaddownloadimagedata.getImageBuffer() instanceof CapeImageBuffer) {
                            final CapeImageBuffer capeimagebuffer1 = (CapeImageBuffer) threaddownloadimagedata.getImageBuffer();
                            player.setElytraOfCape(capeimagebuffer1.isElytraOfCape());
                        }
                    }

                    return;
                }
            }

            final CapeImageBuffer capeimagebuffer = new CapeImageBuffer(player, resourcelocation);
            final ThreadDownloadImageData threaddownloadimagedata1 = new ThreadDownloadImageData(null, s1, null, capeimagebuffer);
            threaddownloadimagedata1.pipeline = true;
            texturemanager.loadTexture(resourcelocation, threaddownloadimagedata1);
        }
    }

    public static BufferedImage parseCape(final BufferedImage img) {
        int i = 64;
        int j = 32;
        final int k = img.getWidth();

        for (final int l = img.getHeight(); i < k || j < l; j *= 2) {
            i *= 2;
        }

        final BufferedImage bufferedimage = new BufferedImage(i, j, 2);
        final Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(img, 0, 0, null);
        graphics.dispose();
        return bufferedimage;
    }

    public static boolean isElytraCape(final BufferedImage imageRaw, final BufferedImage imageFixed) {
        return imageRaw.getWidth() > imageFixed.getHeight();
    }

    public static void reloadCape(final AbstractClientPlayer player) {
        final String s = player.getNameClear();
        final ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s);
        final TextureManager texturemanager = Config.getTextureManager();
        final ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);

        if (itextureobject instanceof SimpleTexture) {
            final SimpleTexture simpletexture = (SimpleTexture) itextureobject;
            simpletexture.deleteGlTexture();
            texturemanager.deleteTexture(resourcelocation);
        }

        player.setLocationOfCape(null);
        player.setElytraOfCape(false);
        downloadCape(player);
    }
}
