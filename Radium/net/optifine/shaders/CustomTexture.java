// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.shaders;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.texture.ITextureObject;

public class CustomTexture implements ICustomTexture
{
    private int textureUnit;
    private String path;
    private ITextureObject texture;
    
    public CustomTexture(final int textureUnit, final String path, final ITextureObject texture) {
        this.textureUnit = -1;
        this.path = null;
        this.texture = null;
        this.textureUnit = textureUnit;
        this.path = path;
        this.texture = texture;
    }
    
    @Override
    public int getTextureUnit() {
        return this.textureUnit;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public ITextureObject getTexture() {
        return this.texture;
    }
    
    @Override
    public int getTextureId() {
        return this.texture.getGlTextureId();
    }
    
    @Override
    public void deleteTexture() {
        TextureUtil.deleteTexture(this.texture.getGlTextureId());
    }
    
    @Override
    public int getTarget() {
        return 3553;
    }
    
    @Override
    public String toString() {
        return "textureUnit: " + this.textureUnit + ", path: " + this.path + ", glTextureId: " + this.getTextureId();
    }
}
