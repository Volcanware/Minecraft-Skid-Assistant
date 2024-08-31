// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiHopper extends GuiContainer
{
    private static final ResourceLocation HOPPER_GUI_TEXTURE;
    private IInventory playerInventory;
    private IInventory hopperInventory;
    
    static {
        HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
    }
    
    public GuiHopper(final InventoryPlayer playerInv, final IInventory hopperInv) {
        super(new ContainerHopper(playerInv, hopperInv, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = playerInv;
        this.hopperInventory = hopperInv;
        this.allowUserInput = false;
        this.ySize = 133;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        this.fontRendererObj.drawString(this.hopperInventory.getDisplayName().getUnformattedText(), 8.0f, 6.0f, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8.0f, (float)(this.ySize - 96 + 2), 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiHopper.HOPPER_GUI_TEXTURE);
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        Gui.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
