package net.optifine;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class EmissiveTextures {
    private static String suffixEmissive = null;
    private static String suffixEmissivePng = null;
    private static boolean active = false;
    private static boolean render = false;
    private static boolean hasEmissive = false;
    private static boolean renderEmissive = false;
    private static float lightMapX;
    private static float lightMapY;
    private static final String SUFFIX_PNG = ".png";
    private static final ResourceLocation LOCATION_EMPTY = new ResourceLocation("mcpatcher/ctm/default/empty.png");

    public static boolean isActive() {
        return active;
    }

    public static String getSuffixEmissive() {
        return suffixEmissive;
    }

    public static void beginRender() {
        render = true;
        hasEmissive = false;
    }

    public static ITextureObject getEmissiveTexture(final ITextureObject texture, final Map<ResourceLocation, ITextureObject> mapTextures) {
        if (!render) {
            return texture;
        } else if (!(texture instanceof SimpleTexture)) {
            return texture;
        } else {
            final SimpleTexture simpletexture = (SimpleTexture) texture;
            ResourceLocation resourcelocation = simpletexture.locationEmissive;

            if (!renderEmissive) {
                if (resourcelocation != null) {
                    hasEmissive = true;
                }

                return texture;
            } else {
                if (resourcelocation == null) {
                    resourcelocation = LOCATION_EMPTY;
                }

                ITextureObject itextureobject = mapTextures.get(resourcelocation);

                if (itextureobject == null) {
                    itextureobject = new SimpleTexture(resourcelocation);
                    final TextureManager texturemanager = Config.getTextureManager();
                    texturemanager.loadTexture(resourcelocation, itextureobject);
                }

                return itextureobject;
            }
        }
    }

    public static boolean hasEmissive() {
        return hasEmissive;
    }

    public static void beginRenderEmissive() {
        lightMapX = OpenGlHelper.lastBrightnessX;
        lightMapY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, lightMapY);
        renderEmissive = true;
    }

    public static void endRenderEmissive() {
        renderEmissive = false;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapX, lightMapY);
    }

    public static void endRender() {
        render = false;
        hasEmissive = false;
    }

    public static void update() {
        active = false;
        suffixEmissive = null;
        suffixEmissivePng = null;

        if (Config.isEmissiveTextures()) {
            try {
                final String s = "optifine/emissive.properties";
                final ResourceLocation resourcelocation = new ResourceLocation(s);
                final InputStream inputstream = Config.getResourceStream(resourcelocation);

                if (inputstream == null) {
                    return;
                }

                dbg("Loading " + s);
                final Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                suffixEmissive = properties.getProperty("suffix.emissive");

                if (suffixEmissive != null) {
                    suffixEmissivePng = suffixEmissive + ".png";
                }

                active = suffixEmissive != null;
            } catch (final FileNotFoundException var4) {
                return;
            } catch (final IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    private static void dbg(final String str) {
        Config.dbg("EmissiveTextures: " + str);
    }

    private static void warn(final String str) {
        Config.warn("EmissiveTextures: " + str);
    }

    public static boolean isEmissive(final ResourceLocation loc) {
        return suffixEmissivePng != null && loc.getResourcePath().endsWith(suffixEmissivePng);
    }

    public static void loadTexture(final ResourceLocation loc, final SimpleTexture tex) {
        if (loc != null && tex != null) {
            tex.isEmissive = false;
            tex.locationEmissive = null;

            if (suffixEmissivePng != null) {
                final String s = loc.getResourcePath();

                if (s.endsWith(".png")) {
                    if (s.endsWith(suffixEmissivePng)) {
                        tex.isEmissive = true;
                    } else {
                        final String s1 = s.substring(0, s.length() - ".png".length()) + suffixEmissivePng;
                        final ResourceLocation resourcelocation = new ResourceLocation(loc.getResourceDomain(), s1);

                        if (Config.hasResource(resourcelocation)) {
                            tex.locationEmissive = resourcelocation;
                        }
                    }
                }
            }
        }
    }
}
