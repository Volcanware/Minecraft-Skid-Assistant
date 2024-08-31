// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.gui;

import net.optifine.shaders.Shaders;
import net.minecraft.src.Config;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.GuiChat;

public class GuiChatOF extends GuiChat
{
    private static final String CMD_RELOAD_SHADERS = "/reloadShaders";
    private static final String CMD_RELOAD_CHUNKS = "/reloadChunks";
    
    public GuiChatOF(final GuiChat guiChat) {
        super(GuiVideoSettings.getGuiChatText(guiChat));
    }
    
    @Override
    public void sendChatMessage(final String msg) {
        if (this.checkCustomCommand(msg)) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }
        else {
            super.sendChatMessage(msg);
        }
    }
    
    private boolean checkCustomCommand(String msg) {
        if (msg == null) {
            return false;
        }
        msg = msg.trim();
        if (msg.equals("/reloadShaders")) {
            if (Config.isShaders()) {
                Shaders.uninit();
                Shaders.loadShaderPack();
            }
            return true;
        }
        if (msg.equals("/reloadChunks")) {
            this.mc.renderGlobal.loadRenderers();
            return true;
        }
        return false;
    }
}
