// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.player;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.DynamicTexture;
import java.awt.image.BufferedImage;
import net.minecraft.util.ResourceLocation;
import java.awt.Dimension;

public class PlayerItemModel
{
    private Dimension textureSize;
    private boolean usePlayerTexture;
    private PlayerItemRenderer[] modelRenderers;
    private ResourceLocation textureLocation;
    private BufferedImage textureImage;
    private DynamicTexture texture;
    private ResourceLocation locationMissing;
    public static final int ATTACH_BODY = 0;
    public static final int ATTACH_HEAD = 1;
    public static final int ATTACH_LEFT_ARM = 2;
    public static final int ATTACH_RIGHT_ARM = 3;
    public static final int ATTACH_LEFT_LEG = 4;
    public static final int ATTACH_RIGHT_LEG = 5;
    public static final int ATTACH_CAPE = 6;
    
    public PlayerItemModel(final Dimension textureSize, final boolean usePlayerTexture, final PlayerItemRenderer[] modelRenderers) {
        this.textureSize = null;
        this.usePlayerTexture = false;
        this.modelRenderers = new PlayerItemRenderer[0];
        this.textureLocation = null;
        this.textureImage = null;
        this.texture = null;
        this.locationMissing = new ResourceLocation("textures/blocks/wool_colored_red.png");
        this.textureSize = textureSize;
        this.usePlayerTexture = usePlayerTexture;
        this.modelRenderers = modelRenderers;
    }
    
    public void render(final ModelBiped modelBiped, final AbstractClientPlayer player, final float scale, final float partialTicks) {
        final TextureManager texturemanager = Config.getTextureManager();
        if (this.usePlayerTexture) {
            texturemanager.bindTexture(player.getLocationSkin());
        }
        else if (this.textureLocation != null) {
            if (this.texture == null && this.textureImage != null) {
                this.texture = new DynamicTexture(this.textureImage);
                Minecraft.getMinecraft().getTextureManager().loadTexture(this.textureLocation, this.texture);
            }
            texturemanager.bindTexture(this.textureLocation);
        }
        else {
            texturemanager.bindTexture(this.locationMissing);
        }
        for (int i = 0; i < this.modelRenderers.length; ++i) {
            final PlayerItemRenderer playeritemrenderer = this.modelRenderers[i];
            GL11.glPushMatrix();
            if (player.isSneaking()) {
                GL11.glTranslatef(0.0f, 0.2f, 0.0f);
            }
            playeritemrenderer.render(modelBiped, scale);
            GL11.glPopMatrix();
        }
    }
    
    public static ModelRenderer getAttachModel(final ModelBiped modelBiped, final int attachTo) {
        switch (attachTo) {
            case 0: {
                return modelBiped.bipedBody;
            }
            case 1: {
                return modelBiped.bipedHead;
            }
            case 2: {
                return modelBiped.bipedLeftArm;
            }
            case 3: {
                return modelBiped.bipedRightArm;
            }
            case 4: {
                return modelBiped.bipedLeftLeg;
            }
            case 5: {
                return modelBiped.bipedRightLeg;
            }
            default: {
                return null;
            }
        }
    }
    
    public BufferedImage getTextureImage() {
        return this.textureImage;
    }
    
    public void setTextureImage(final BufferedImage textureImage) {
        this.textureImage = textureImage;
    }
    
    public DynamicTexture getTexture() {
        return this.texture;
    }
    
    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }
    
    public void setTextureLocation(final ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }
    
    public boolean isUsePlayerTexture() {
        return this.usePlayerTexture;
    }
}
