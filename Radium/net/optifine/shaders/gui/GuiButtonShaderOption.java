// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.shaders.gui;

import net.optifine.shaders.config.ShaderOption;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonShaderOption extends GuiButton
{
    private ShaderOption shaderOption;
    
    public GuiButtonShaderOption(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final ShaderOption shaderOption, final String text) {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.shaderOption = null;
        this.shaderOption = shaderOption;
    }
    
    public ShaderOption getShaderOption() {
        return this.shaderOption;
    }
    
    public void valueChanged() {
    }
    
    public boolean isSwitchable() {
        return true;
    }
}
