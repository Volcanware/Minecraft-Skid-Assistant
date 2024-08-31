// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import java.util.List;
import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Slot;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.ICrafting;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiRepair extends GuiContainer implements ICrafting
{
    private static final ResourceLocation anvilResource;
    private ContainerRepair anvil;
    private GuiTextField nameField;
    private InventoryPlayer playerInventory;
    
    static {
        anvilResource = new ResourceLocation("textures/gui/container/anvil.png");
    }
    
    public GuiRepair(final InventoryPlayer inventoryIn, final World worldIn) {
        super(new ContainerRepair(inventoryIn, worldIn, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = inventoryIn;
        this.anvil = (ContainerRepair)this.inventorySlots;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        (this.nameField = new GuiTextField(0, this.fontRendererObj, i + 62, j + 24, 103, 12)).setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(30);
        this.inventorySlots.removeCraftingFromCrafters(this);
        this.inventorySlots.onCraftGuiOpened(this);
    }
    
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.inventorySlots.removeCraftingFromCrafters(this);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.fontRendererObj.drawString(I18n.format("container.repair", new Object[0]), 60.0f, 6.0f, 4210752);
        if (this.anvil.maximumCost > 0) {
            int i = 8453920;
            boolean flag = true;
            String s = I18n.format("container.repair.cost", this.anvil.maximumCost);
            if (this.anvil.maximumCost >= 40 && !this.mc.thePlayer.capabilities.isCreativeMode) {
                s = I18n.format("container.repair.expensive", new Object[0]);
                i = 16736352;
            }
            else if (!this.anvil.getSlot(2).getHasStack()) {
                flag = false;
            }
            else if (!this.anvil.getSlot(2).canTakeStack(this.playerInventory.player)) {
                i = 16736352;
            }
            if (flag) {
                final int j = 0xFF000000 | (i & 0xFCFCFC) >> 2 | (i & 0xFF000000);
                final int k = this.xSize - 8 - this.fontRendererObj.getStringWidth(s);
                final int l = 67;
                if (this.fontRendererObj.getUnicodeFlag()) {
                    Gui.drawRect((float)(k - 3), (float)(l - 2), (float)(this.xSize - 7), (float)(l + 10), -16777216);
                    Gui.drawRect((float)(k - 2), (float)(l - 1), (float)(this.xSize - 8), (float)(l + 9), -12895429);
                }
                else {
                    this.fontRendererObj.drawString(s, (float)k, (float)(l + 1), j);
                    this.fontRendererObj.drawString(s, (float)(k + 1), (float)l, j);
                    this.fontRendererObj.drawString(s, (float)(k + 1), (float)(l + 1), j);
                }
                this.fontRendererObj.drawString(s, (float)k, (float)l, i);
            }
        }
        GlStateManager.enableLighting();
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {
            this.renameItem();
        }
        else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    private void renameItem() {
        String s = this.nameField.getText();
        final Slot slot = this.anvil.getSlot(0);
        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName())) {
            s = "";
        }
        this.anvil.updateItemName(s);
        this.mc.thePlayer.sendQueue.sendPacket(new C17PacketCustomPayload("MC|ItemName", new PacketBuffer(Unpooled.buffer()).writeString(s)));
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.nameField.drawTextBox();
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiRepair.anvilResource);
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        Gui.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        Gui.drawTexturedModalRect(i + 59, j + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
        if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack()) {
            Gui.drawTexturedModalRect(i + 99, j + 45, this.xSize, 0, 28, 21);
        }
    }
    
    @Override
    public void updateCraftingInventory(final Container containerToSend, final List<ItemStack> itemsList) {
        this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
    }
    
    @Override
    public void sendSlotContents(final Container containerToSend, final int slotInd, final ItemStack stack) {
        if (slotInd == 0) {
            this.nameField.setText((stack == null) ? "" : stack.getDisplayName());
            this.nameField.setEnabled(stack != null);
            if (stack != null) {
                this.renameItem();
            }
        }
    }
    
    @Override
    public void sendProgressBarUpdate(final Container containerIn, final int varToUpdate, final int newValue) {
    }
    
    @Override
    public void func_175173_a(final Container p_175173_1_, final IInventory p_175173_2_) {
    }
}
