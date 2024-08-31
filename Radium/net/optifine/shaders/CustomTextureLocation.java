// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.shaders;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

public class CustomTextureLocation implements ICustomTexture
{
    private int textureUnit;
    private ResourceLocation location;
    private int variant;
    private ITextureObject texture;
    public static final int VARIANT_BASE = 0;
    public static final int VARIANT_NORMAL = 1;
    public static final int VARIANT_SPECULAR = 2;
    
    public CustomTextureLocation(final int textureUnit, final ResourceLocation location, final int variant) {
        this.textureUnit = -1;
        this.variant = 0;
        this.textureUnit = textureUnit;
        this.location = location;
        this.variant = variant;
    }
    
    public ITextureObject getTexture() {
        if (this.texture == null) {
            final TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            this.texture = texturemanager.getTexture(this.location);
            if (this.texture == null) {
                this.texture = new SimpleTexture(this.location);
                texturemanager.loadTexture(this.location, this.texture);
                this.texture = texturemanager.getTexture(this.location);
            }
        }
        return this.texture;
    }
    
    @Override
    public int getTextureId() {
        final ITextureObject itextureobject = this.getTexture();
        if (this.variant != 0 && itextureobject instanceof AbstractTexture) {
            final AbstractTexture abstracttexture = (AbstractTexture)itextureobject;
            final MultiTexID multitexid = abstracttexture.multiTex;
            if (multitexid != null) {
                if (this.variant == 1) {
                    return multitexid.norm;
                }
                if (this.variant == 2) {
                    return multitexid.spec;
                }
            }
        }
        return itextureobject.getGlTextureId();
    }
    
    @Override
    public int getTextureUnit() {
        return this.textureUnit;
    }
    
    @Override
    public void deleteTexture() {
    }
    
    @Override
    public int getTarget() {
        return 3553;
    }
    
    @Override
    public String toString() {
        return "textureUnit: " + this.textureUnit + ", location: " + this.location + ", glTextureId: " + ((this.texture != null) ? Integer.valueOf(this.texture.getGlTextureId()) : "");
    }
}
