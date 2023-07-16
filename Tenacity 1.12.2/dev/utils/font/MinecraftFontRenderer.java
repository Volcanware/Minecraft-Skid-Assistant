package dev.utils.font;

import dev.client.tenacity.utils.render.RenderUtil;
import dev.utils.font.CFont;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class MinecraftFontRenderer
extends CFont {
    CFont.CharData[] boldChars = new CFont.CharData[256];
    CFont.CharData[] italicChars = new CFont.CharData[256];
    CFont.CharData[] boldItalicChars = new CFont.CharData[256];
    int[] colorCode = new int[32];
    DynamicTexture texBold;
    DynamicTexture texItalic;
    DynamicTexture texItalicBold;
    String colorcodeIdentifiers = "0123456789abcdefklmnor";

    public MinecraftFontRenderer(Font font) {
        super(font, true, true);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public MinecraftFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }

    public int drawStringWithShadow(String text, double x2, double y2, int color) {
        float shadowWidth = this.drawString(text, x2 + 0.5, y2 + 0.5, color, true, 8.3f, false);
        return (int)Math.max(shadowWidth, this.drawString(text, x2, y2, color, false, 8.3f, false));
    }

    public int drawString(String text, double x2, double y2, int color) {
        return (int)this.drawString(text, x2, y2, color, false, 8.3f, false);
    }

    public int drawPassword(String text, double x2, float y2, int color) {
        return (int)this.drawString(text.replaceAll("\\.", "."), x2, y2, color, false, 8.0f, false);
    }

    public void drawSmoothString(String text, double x2, float y2, int color) {
        this.drawString(text, x2, y2, color, false, 8.3f, true);
    }

    public void drawSmoothStringWithShadow(String text, double x2, float y2, int color) {
        this.drawString(text, x2 + 0.5, y2 + 0.5f, color, true, 8.3f, true);
        this.drawString(text, x2, y2, color, false, 8.3f, true);
    }

    public double getPasswordWidth(String text) {
        return this.getStringWidth(text.replaceAll("\\.", "."), 8.0f);
    }

    public float drawCenteredString(String text, float x2, float y2, int color) {
        return this.drawString(text, x2 - (float)(this.getStringWidth(text) / 2.0), y2, color);
    }

    public float drawCenteredStringWithShadow(String text, float x2, float y2, int color) {
        return this.drawStringWithShadow(text, x2 - (float)(this.getStringWidth(text) / 2.0), y2, color);
    }

    public float getMiddleOfBox(float boxHeight) {
        return boxHeight / 2.0f - (float)this.getHeight() / 2.0f;
    }

    public float drawString(String text, double x, double y, int color, boolean shadow, float kerning, boolean smooth) {
        if (text == null) {
            return 0.0f;
        }
        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        CFont.CharData[] currentData = this.charData;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        x = (x - 1.0) * 2.0;
        y = (y - 3.0) * 2.0;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        RenderUtil.color(color);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());
        RenderUtil.bindTexture(this.tex.getGlTextureId());
        if (smooth) {
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        } else {
            GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        }
        int index = 0;
        while (true) {
            if (index >= text.length()) {
                GL11.glHint((int)3155, (int)4352);
                GL11.glPopMatrix();
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GlStateManager.bindTexture(0);
                return (float)x / 2.0f;
            }
            char character = text.charAt(index);
            if (character == '\u00a7') {
                int colorIndex = 21;
                try {
                    colorIndex = this.colorcodeIdentifiers.indexOf(text.charAt(index + 1));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    RenderUtil.color(this.colorCode[colorIndex], alpha);
                } else {
                    switch (colorIndex) {
                        case 17: {
                            bold = true;
                            if (italic) {
                                GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                                currentData = this.boldItalicChars;
                                break;
                            }
                            GlStateManager.bindTexture(this.texBold.getGlTextureId());
                            currentData = this.boldChars;
                            break;
                        }
                        case 18: {
                            strikethrough = true;
                            break;
                        }
                        case 19: {
                            underline = true;
                            break;
                        }
                        case 20: {
                            italic = true;
                            if (bold) {
                                GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                                currentData = this.boldItalicChars;
                                break;
                            }
                            GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                            currentData = this.italicChars;
                            break;
                        }
                        default: {
                            bold = false;
                            italic = false;
                            underline = false;
                            strikethrough = false;
                            RenderUtil.color(color);
                            GlStateManager.bindTexture(this.tex.getGlTextureId());
                            currentData = this.charData;
                        }
                    }
                }
                ++index;
            } else if (character < currentData.length) {
                this.drawLetter(x, y, currentData, strikethrough, underline, character);
                x += (double)((float)currentData[character].width - kerning + (float)this.charOffset);
            }
            ++index;
        }
    }

    private void drawLetter(double x, double y, CFont.CharData[] currentData, boolean strikethrough, boolean underline, char character) {
        GL11.glBegin((int)4);
        this.drawChar(currentData, character, (float)x, (float)y);
        GL11.glEnd();
        if (strikethrough) {
            this.drawLine(x, y + (double)(currentData[character].height / 2), x + (double)currentData[character].width - 8.0, y + (double)(currentData[character].height / 2));
        }
        if (!underline) return;
        this.drawLine(x, y + (double)currentData[character].height - 2.0, x + (double)currentData[character].width - 8.0, y + (double)currentData[character].height - 2.0);
    }

    public double getStringWidth(String text) {
        if (text == null) {
            return 0.0;
        }
        float width = 0.0f;
        CFont.CharData[] currentData = this.charData;
        int index = 0;
        while (index < text.length()) {
            char character = text.charAt(index);
            if (character == '\u00a7') {
                ++index;
            } else if (character < currentData.length) {
                width += (float)currentData[character].width - 8.3f + (float)this.charOffset;
            }
            ++index;
        }
        return width / 2.0f;
    }

    public double getStringWidth(String text, float kerning) {
        if (text == null) {
            return 0.0;
        }
        float width = 0.0f;
        CFont.CharData[] currentData = this.charData;
        int index = 0;
        while (index < text.length()) {
            char c = text.charAt(index);
            if (c == '\u00a7') {
                ++index;
            } else if (c < currentData.length) {
                width += (float)currentData[c].width - kerning + (float)this.charOffset;
            }
            ++index;
        }
        return width / 2.0f;
    }

    public int getHeight() {
        return (this.fontHeight - 8) / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        this.texItalicBold = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void drawLine(double x2, double y2, double x1, double y1) {
        GL11.glDisable((int)3553);
        GL11.glLineWidth((float)1.0f);
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glEnd();
        GL11.glEnable((int)3553);
    }

    public void wrapText(String text, float x, float y, int color, float width, float heightIncrement) {
        ArrayList<String> lines = new ArrayList<String>();
        String[] words = text.trim().split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            float totalWidth = (float)this.getStringWidth(line + " " + word);
            if (x + totalWidth >= x + width) {
                lines.add(line.toString());
                line = new StringBuilder(word).append(" ");
                continue;
            }
            line.append(word).append(" ");
        }
        lines.add(line.toString());
        float newY = y;
        Iterator iterator = lines.iterator();
        while (iterator.hasNext()) {
            String s = (String)iterator.next();
            RenderUtil.resetColor();
            this.drawString(s, x, newY, color);
            newY += (float)this.getHeight() + heightIncrement;
        }
    }

    private void setupMinecraftColorcodes() {
        int index = 0;
        while (index < 32) {
            int noClue = (index >> 3 & 1) * 85;
            int red = (index >> 2 & 1) * 170 + noClue;
            int green = (index >> 1 & 1) * 170 + noClue;
            int blue = (index & 1) * 170 + noClue;
            if (index == 6) {
                red += 85;
            }
            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCode[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
            ++index;
        }
    }

    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidthPassword(String text, int width, boolean custom) {
        text = text.replaceAll("\\.", ".");
        return this.trimStringToWidth(text, width, custom);
    }

    private float getCharWidthFloat(char c) {
        if (c == '\u00a7') {
            return -1.0f;
        }
        if (c == ' ') {
            return 2.0f;
        }
        int var2 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c);
        if (c > '\u0000' && var2 != -1) {
            return (float)this.charData[var2].width / 2.0f - 4.0f;
        }
        if ((float)this.charData[c].width / 2.0f - 4.0f == 0.0f) return 0.0f;
        int var3 = (int)((float)this.charData[c].width / 2.0f - 4.0f) >>> 4;
        int var4 = (int)((float)this.charData[c].width / 2.0f - 4.0f) & 0xF;
        return (++var4 - (var3 &= 0xF)) / 2 + 1;
    }

    public String trimStringToWidth(String text, int width, boolean custom) {
        StringBuilder buffer = new StringBuilder();
        float lineWidth = 0.0f;
        int offset = custom ? text.length() - 1 : 0;
        int increment = custom ? -1 : 1;
        boolean var8 = false;
        boolean var9 = false;
        int index = offset;
        while (index >= 0) {
            if (index >= text.length()) return buffer.toString();
            if (!(lineWidth < (float)width)) return buffer.toString();
            char character = text.charAt(index);
            float charWidth = this.getCharWidthFloat(character);
            if (var8) {
                var8 = false;
                if (character != 'l' && character != 'L') {
                    if (character == 'r' || character == 'R') {
                        var9 = false;
                    }
                } else {
                    var9 = true;
                }
            } else if (charWidth < 0.0f) {
                var8 = true;
            } else {
                lineWidth += charWidth;
                if (var9) {
                    lineWidth += 1.0f;
                }
            }
            if (lineWidth > (float)width) {
                return buffer.toString();
            }
            if (custom) {
                buffer.insert(0, character);
            } else {
                buffer.append(character);
            }
            index += increment;
        }
        return buffer.toString();
    }
}