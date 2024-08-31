// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.renderer.texture;

import net.optifine.shaders.Shaders;
import java.util.Collection;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import net.optifine.util.CounterInt;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.resources.data.AnimationFrame;
import net.optifine.util.TextureUtils;
import java.awt.image.BufferedImage;
import net.optifine.SmartAnimations;
import net.minecraft.util.ResourceLocation;
import net.minecraft.src.Config;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import java.util.List;

public class TextureAtlasSprite
{
    private final String iconName;
    protected List<int[][]> framesTextureData;
    protected int[][] interpolatedFrameData;
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    protected int originX;
    protected int originY;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;
    private static String locationNameClock;
    private static String locationNameCompass;
    private int indexInMap;
    public float baseU;
    public float baseV;
    public int sheetWidth;
    public int sheetHeight;
    public int glSpriteTextureId;
    public TextureAtlasSprite spriteSingle;
    public boolean isSpriteSingle;
    public int mipmapLevels;
    public TextureAtlasSprite spriteNormal;
    public TextureAtlasSprite spriteSpecular;
    public boolean isShadersSprite;
    public boolean isEmissive;
    public TextureAtlasSprite spriteEmissive;
    private int animationIndex;
    private boolean animationActive;
    
    static {
        TextureAtlasSprite.locationNameClock = "builtin/clock";
        TextureAtlasSprite.locationNameCompass = "builtin/compass";
    }
    
    private TextureAtlasSprite(final String p_i7_1_, final boolean p_i7_2_) {
        this.framesTextureData = (List<int[][]>)Lists.newArrayList();
        this.indexInMap = -1;
        this.glSpriteTextureId = -1;
        this.spriteSingle = null;
        this.isSpriteSingle = false;
        this.mipmapLevels = 0;
        this.spriteNormal = null;
        this.spriteSpecular = null;
        this.isShadersSprite = false;
        this.isEmissive = false;
        this.spriteEmissive = null;
        this.animationIndex = -1;
        this.animationActive = false;
        this.iconName = p_i7_1_;
        this.isSpriteSingle = p_i7_2_;
    }
    
    public TextureAtlasSprite(final String spriteName) {
        this.framesTextureData = (List<int[][]>)Lists.newArrayList();
        this.indexInMap = -1;
        this.glSpriteTextureId = -1;
        this.spriteSingle = null;
        this.isSpriteSingle = false;
        this.mipmapLevels = 0;
        this.spriteNormal = null;
        this.spriteSpecular = null;
        this.isShadersSprite = false;
        this.isEmissive = false;
        this.spriteEmissive = null;
        this.animationIndex = -1;
        this.animationActive = false;
        this.iconName = spriteName;
        if (Config.isMultiTexture()) {
            this.spriteSingle = new TextureAtlasSprite(String.valueOf(this.getIconName()) + ".spriteSingle", true);
        }
    }
    
    protected static TextureAtlasSprite makeAtlasSprite(final ResourceLocation spriteResourceLocation) {
        final String s = spriteResourceLocation.toString();
        return TextureAtlasSprite.locationNameClock.equals(s) ? new TextureClock(s) : (TextureAtlasSprite.locationNameCompass.equals(s) ? new TextureCompass(s) : new TextureAtlasSprite(s));
    }
    
    public static void setLocationNameClock(final String clockName) {
        TextureAtlasSprite.locationNameClock = clockName;
    }
    
    public static void setLocationNameCompass(final String compassName) {
        TextureAtlasSprite.locationNameCompass = compassName;
    }
    
    public void initSprite(final int inX, final int inY, final int originInX, final int originInY, final boolean rotatedIn) {
        this.originX = originInX;
        this.originY = originInY;
        this.rotated = rotatedIn;
        final float f = (float)(0.009999999776482582 / inX);
        final float f2 = (float)(0.009999999776482582 / inY);
        this.minU = originInX / (float)inX + f;
        this.maxU = (originInX + this.width) / (float)inX - f;
        this.minV = originInY / (float)inY + f2;
        this.maxV = (originInY + this.height) / (float)inY - f2;
        this.baseU = Math.min(this.minU, this.maxU);
        this.baseV = Math.min(this.minV, this.maxV);
        if (this.spriteSingle != null) {
            this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
        }
        if (this.spriteNormal != null) {
            this.spriteNormal.copyFrom(this);
        }
        if (this.spriteSpecular != null) {
            this.spriteSpecular.copyFrom(this);
        }
    }
    
    public void copyFrom(final TextureAtlasSprite atlasSpirit) {
        this.originX = atlasSpirit.originX;
        this.originY = atlasSpirit.originY;
        this.width = atlasSpirit.width;
        this.height = atlasSpirit.height;
        this.rotated = atlasSpirit.rotated;
        this.minU = atlasSpirit.minU;
        this.maxU = atlasSpirit.maxU;
        this.minV = atlasSpirit.minV;
        this.maxV = atlasSpirit.maxV;
        if (atlasSpirit != Config.getTextureMap().getMissingSprite()) {
            this.indexInMap = atlasSpirit.indexInMap;
        }
        this.baseU = atlasSpirit.baseU;
        this.baseV = atlasSpirit.baseV;
        this.sheetWidth = atlasSpirit.sheetWidth;
        this.sheetHeight = atlasSpirit.sheetHeight;
        this.glSpriteTextureId = atlasSpirit.glSpriteTextureId;
        this.mipmapLevels = atlasSpirit.mipmapLevels;
        if (this.spriteSingle != null) {
            this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
        }
        this.animationIndex = atlasSpirit.animationIndex;
    }
    
    public int getOriginX() {
        return this.originX;
    }
    
    public int getOriginY() {
        return this.originY;
    }
    
    public int getIconWidth() {
        return this.width;
    }
    
    public int getIconHeight() {
        return this.height;
    }
    
    public float getMinU() {
        return this.minU;
    }
    
    public float getMaxU() {
        return this.maxU;
    }
    
    public float getInterpolatedU(final double u) {
        final float f = this.maxU - this.minU;
        return this.minU + f * (float)u / 16.0f;
    }
    
    public float getMinV() {
        return this.minV;
    }
    
    public float getMaxV() {
        return this.maxV;
    }
    
    public float getInterpolatedV(final double v) {
        final float f = this.maxV - this.minV;
        return this.minV + f * ((float)v / 16.0f);
    }
    
    public String getIconName() {
        return this.iconName;
    }
    
    public void updateAnimation() {
        if (this.animationMetadata != null) {
            this.animationActive = (!SmartAnimations.isActive() || SmartAnimations.isSpriteRendered(this.animationIndex));
            ++this.tickCounter;
            if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
                final int i = this.animationMetadata.getFrameIndex(this.frameCounter);
                final int j = (this.animationMetadata.getFrameCount() == 0) ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
                this.frameCounter = (this.frameCounter + 1) % j;
                this.tickCounter = 0;
                final int k = this.animationMetadata.getFrameIndex(this.frameCounter);
                final boolean flag = false;
                final boolean flag2 = this.isSpriteSingle;
                if (!this.animationActive) {
                    return;
                }
                if (i != k && k >= 0 && k < this.framesTextureData.size()) {
                    TextureUtil.uploadTextureMipmap(this.framesTextureData.get(k), this.width, this.height, this.originX, this.originY, flag, flag2);
                }
            }
            else if (this.animationMetadata.isInterpolate()) {
                if (!this.animationActive) {
                    return;
                }
                this.updateAnimationInterpolated();
            }
        }
    }
    
    private void updateAnimationInterpolated() {
        final double d0 = 1.0 - this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
        final int i = this.animationMetadata.getFrameIndex(this.frameCounter);
        final int j = (this.animationMetadata.getFrameCount() == 0) ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
        final int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);
        if (i != k && k >= 0 && k < this.framesTextureData.size()) {
            final int[][] aint = this.framesTextureData.get(i);
            final int[][] aint2 = this.framesTextureData.get(k);
            if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != aint.length) {
                this.interpolatedFrameData = new int[aint.length][];
            }
            for (int l = 0; l < aint.length; ++l) {
                if (this.interpolatedFrameData[l] == null) {
                    this.interpolatedFrameData[l] = new int[aint[l].length];
                }
                if (l < aint2.length && aint2[l].length == aint[l].length) {
                    for (int i2 = 0; i2 < aint[l].length; ++i2) {
                        final int j2 = aint[l][i2];
                        final int k2 = aint2[l][i2];
                        final int l2 = (int)(((j2 & 0xFF0000) >> 16) * d0 + ((k2 & 0xFF0000) >> 16) * (1.0 - d0));
                        final int i3 = (int)(((j2 & 0xFF00) >> 8) * d0 + ((k2 & 0xFF00) >> 8) * (1.0 - d0));
                        final int j3 = (int)((j2 & 0xFF) * d0 + (k2 & 0xFF) * (1.0 - d0));
                        this.interpolatedFrameData[l][i2] = ((j2 & 0xFF000000) | l2 << 16 | i3 << 8 | j3);
                    }
                }
            }
            TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
        }
    }
    
    public int[][] getFrameTextureData(final int index) {
        return this.framesTextureData.get(index);
    }
    
    public int getFrameCount() {
        return this.framesTextureData.size();
    }
    
    public void setIconWidth(final int newWidth) {
        this.width = newWidth;
        if (this.spriteSingle != null) {
            this.spriteSingle.setIconWidth(this.width);
        }
    }
    
    public void setIconHeight(final int newHeight) {
        this.height = newHeight;
        if (this.spriteSingle != null) {
            this.spriteSingle.setIconHeight(this.height);
        }
    }
    
    public void loadSprite(final BufferedImage[] images, final AnimationMetadataSection meta) throws IOException {
        this.resetSprite();
        final int i = images[0].getWidth();
        final int j = images[0].getHeight();
        this.width = i;
        this.height = j;
        if (this.spriteSingle != null) {
            this.spriteSingle.width = this.width;
            this.spriteSingle.height = this.height;
        }
        final int[][] aint = new int[images.length][];
        for (int k = 0; k < images.length; ++k) {
            BufferedImage bufferedimage = images[k];
            if (bufferedimage != null) {
                if (this.width >> k != bufferedimage.getWidth()) {
                    bufferedimage = TextureUtils.scaleImage(bufferedimage, this.width >> k);
                }
                if (k > 0 && (bufferedimage.getWidth() != i >> k || bufferedimage.getHeight() != j >> k)) {
                    throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", k, bufferedimage.getWidth(), bufferedimage.getHeight(), i >> k, j >> k));
                }
                aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
                bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
            }
        }
        if (meta == null) {
            if (j != i) {
                throw new RuntimeException("broken aspect ratio and not an animation");
            }
            this.framesTextureData.add(aint);
        }
        else {
            final int j2 = j / i;
            final int l1 = i;
            final int m = i;
            this.height = this.width;
            if (meta.getFrameCount() > 0) {
                for (final int i2 : meta.getFrameIndexSet()) {
                    if (i2 >= j2) {
                        throw new RuntimeException("invalid frameindex " + i2);
                    }
                    this.allocateFrameTextureData(i2);
                    this.framesTextureData.set(i2, getFrameTextureData(aint, l1, m, i2));
                }
                this.animationMetadata = meta;
            }
            else {
                final List<AnimationFrame> list = (List<AnimationFrame>)Lists.newArrayList();
                for (int j3 = 0; j3 < j2; ++j3) {
                    this.framesTextureData.add(getFrameTextureData(aint, l1, m, j3));
                    list.add(new AnimationFrame(j3, -1));
                }
                this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
            }
        }
        if (!this.isShadersSprite) {
            if (Config.isShaders()) {
                this.loadShadersSprites();
            }
            for (int k2 = 0; k2 < this.framesTextureData.size(); ++k2) {
                final int[][] aint2 = this.framesTextureData.get(k2);
                if (aint2 != null && !this.iconName.startsWith("minecraft:blocks/leaves_")) {
                    for (int i3 = 0; i3 < aint2.length; ++i3) {
                        final int[] aint3 = aint2[i3];
                        this.fixTransparentColor(aint3);
                    }
                }
            }
            if (this.spriteSingle != null) {
                this.spriteSingle.loadSprite(images, meta);
            }
        }
    }
    
    public void generateMipmaps(final int level) {
        final List<int[][]> list = (List<int[][]>)Lists.newArrayList();
        for (int i = 0; i < this.framesTextureData.size(); ++i) {
            final int[][] aint = this.framesTextureData.get(i);
            if (aint != null) {
                try {
                    list.add(TextureUtil.generateMipmapData(level, this.width, aint));
                }
                catch (Throwable throwable) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
                    crashreportcategory.addCrashSection("Frame index", i);
                    crashreportcategory.addCrashSectionCallable("Frame sizes", new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            final StringBuilder stringbuilder = new StringBuilder();
                            int[][] val$aint;
                            for (int length = (val$aint = aint).length, i = 0; i < length; ++i) {
                                final int[] aint1 = val$aint[i];
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append(", ");
                                }
                                stringbuilder.append((aint1 == null) ? "null" : Integer.valueOf(aint1.length));
                            }
                            return stringbuilder.toString();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }
        this.setFramesTextureData(list);
        if (this.spriteSingle != null) {
            this.spriteSingle.generateMipmaps(level);
        }
    }
    
    private void allocateFrameTextureData(final int index) {
        if (this.framesTextureData.size() <= index) {
            for (int i = this.framesTextureData.size(); i <= index; ++i) {
                this.framesTextureData.add(null);
            }
        }
        if (this.spriteSingle != null) {
            this.spriteSingle.allocateFrameTextureData(index);
        }
    }
    
    private static int[][] getFrameTextureData(final int[][] data, final int rows, final int columns, final int p_147962_3_) {
        final int[][] aint = new int[data.length][];
        for (int i = 0; i < data.length; ++i) {
            final int[] aint2 = data[i];
            if (aint2 != null) {
                aint[i] = new int[(rows >> i) * (columns >> i)];
                System.arraycopy(aint2, p_147962_3_ * aint[i].length, aint[i], 0, aint[i].length);
            }
        }
        return aint;
    }
    
    public void clearFramesTextureData() {
        this.framesTextureData.clear();
        if (this.spriteSingle != null) {
            this.spriteSingle.clearFramesTextureData();
        }
    }
    
    public boolean hasAnimationMetadata() {
        return this.animationMetadata != null;
    }
    
    public void setFramesTextureData(final List<int[][]> newFramesTextureData) {
        this.framesTextureData = newFramesTextureData;
        if (this.spriteSingle != null) {
            this.spriteSingle.setFramesTextureData(newFramesTextureData);
        }
    }
    
    private void resetSprite() {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
        if (this.spriteSingle != null) {
            this.spriteSingle.resetSprite();
        }
    }
    
    @Override
    public String toString() {
        return "TextureAtlasSprite{name='" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }
    
    public boolean hasCustomLoader(final IResourceManager p_hasCustomLoader_1_, final ResourceLocation p_hasCustomLoader_2_) {
        return false;
    }
    
    public boolean load(final IResourceManager p_load_1_, final ResourceLocation p_load_2_) {
        return true;
    }
    
    public int getIndexInMap() {
        return this.indexInMap;
    }
    
    public void setIndexInMap(final int p_setIndexInMap_1_) {
        this.indexInMap = p_setIndexInMap_1_;
    }
    
    public void updateIndexInMap(final CounterInt p_updateIndexInMap_1_) {
        if (this.indexInMap < 0) {
            this.indexInMap = p_updateIndexInMap_1_.nextValue();
        }
    }
    
    public int getAnimationIndex() {
        return this.animationIndex;
    }
    
    public void setAnimationIndex(final int p_setAnimationIndex_1_) {
        this.animationIndex = p_setAnimationIndex_1_;
        if (this.spriteNormal != null) {
            this.spriteNormal.setAnimationIndex(p_setAnimationIndex_1_);
        }
        if (this.spriteSpecular != null) {
            this.spriteSpecular.setAnimationIndex(p_setAnimationIndex_1_);
        }
    }
    
    public boolean isAnimationActive() {
        return this.animationActive;
    }
    
    private void fixTransparentColor(final int[] p_fixTransparentColor_1_) {
        if (p_fixTransparentColor_1_ != null) {
            long i = 0L;
            long j = 0L;
            long k = 0L;
            long l = 0L;
            for (int i2 = 0; i2 < p_fixTransparentColor_1_.length; ++i2) {
                final int j2 = p_fixTransparentColor_1_[i2];
                final int k2 = j2 >> 24 & 0xFF;
                if (k2 >= 16) {
                    final int l2 = j2 >> 16 & 0xFF;
                    final int i3 = j2 >> 8 & 0xFF;
                    final int j3 = j2 & 0xFF;
                    i += l2;
                    j += i3;
                    k += j3;
                    ++l;
                }
            }
            if (l > 0L) {
                final int l3 = (int)(i / l);
                final int i4 = (int)(j / l);
                final int j4 = (int)(k / l);
                final int k3 = l3 << 16 | i4 << 8 | j4;
                for (int l4 = 0; l4 < p_fixTransparentColor_1_.length; ++l4) {
                    final int i5 = p_fixTransparentColor_1_[l4];
                    final int k4 = i5 >> 24 & 0xFF;
                    if (k4 <= 16) {
                        p_fixTransparentColor_1_[l4] = k3;
                    }
                }
            }
        }
    }
    
    public double getSpriteU16(final float p_getSpriteU16_1_) {
        final float f = this.maxU - this.minU;
        return (p_getSpriteU16_1_ - this.minU) / f * 16.0f;
    }
    
    public double getSpriteV16(final float p_getSpriteV16_1_) {
        final float f = this.maxV - this.minV;
        return (p_getSpriteV16_1_ - this.minV) / f * 16.0f;
    }
    
    public void bindSpriteTexture() {
        if (this.glSpriteTextureId < 0) {
            TextureUtil.allocateTextureImpl(this.glSpriteTextureId = GL11.glGenTextures(), this.mipmapLevels, this.width, this.height);
            TextureUtils.applyAnisotropicLevel();
        }
        TextureUtils.bindTexture(this.glSpriteTextureId);
    }
    
    public void deleteSpriteTexture() {
        if (this.glSpriteTextureId >= 0) {
            TextureUtil.deleteTexture(this.glSpriteTextureId);
            this.glSpriteTextureId = -1;
        }
    }
    
    public float toSingleU(float p_toSingleU_1_) {
        p_toSingleU_1_ -= this.baseU;
        final float f = this.sheetWidth / (float)this.width;
        p_toSingleU_1_ *= f;
        return p_toSingleU_1_;
    }
    
    public float toSingleV(float p_toSingleV_1_) {
        p_toSingleV_1_ -= this.baseV;
        final float f = this.sheetHeight / (float)this.height;
        p_toSingleV_1_ *= f;
        return p_toSingleV_1_;
    }
    
    public List<int[][]> getFramesTextureData() {
        final List<int[][]> list = new ArrayList<int[][]>();
        list.addAll(this.framesTextureData);
        return list;
    }
    
    public AnimationMetadataSection getAnimationMetadata() {
        return this.animationMetadata;
    }
    
    public void setAnimationMetadata(final AnimationMetadataSection p_setAnimationMetadata_1_) {
        this.animationMetadata = p_setAnimationMetadata_1_;
    }
    
    private void loadShadersSprites() {
        if (Shaders.configNormalMap) {
            final String s = String.valueOf(this.iconName) + "_n";
            ResourceLocation resourcelocation = new ResourceLocation(s);
            resourcelocation = Config.getTextureMap().completeResourceLocation(resourcelocation);
            if (Config.hasResource(resourcelocation)) {
                this.spriteNormal = new TextureAtlasSprite(s);
                this.spriteNormal.isShadersSprite = true;
                this.spriteNormal.copyFrom(this);
                this.spriteNormal.generateMipmaps(this.mipmapLevels);
            }
        }
        if (Shaders.configSpecularMap) {
            final String s2 = String.valueOf(this.iconName) + "_s";
            ResourceLocation resourcelocation2 = new ResourceLocation(s2);
            resourcelocation2 = Config.getTextureMap().completeResourceLocation(resourcelocation2);
            if (Config.hasResource(resourcelocation2)) {
                this.spriteSpecular = new TextureAtlasSprite(s2);
                this.spriteSpecular.isShadersSprite = true;
                this.spriteSpecular.copyFrom(this);
                this.spriteSpecular.generateMipmaps(this.mipmapLevels);
            }
        }
    }
}
