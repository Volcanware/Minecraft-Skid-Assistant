// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.Packet;

public class C0EPacketClickWindow implements Packet<INetHandlerPlayServer>
{
    private int windowId;
    private int slotId;
    private int usedButton;
    private short actionNumber;
    private ItemStack clickedItem;
    private int mode;
    
    public C0EPacketClickWindow() {
    }
    
    public C0EPacketClickWindow(final int windowId, final int slotId, final int usedButton, final int mode, final ItemStack clickedItem, final short actionNumber) {
        this.windowId = windowId;
        this.slotId = slotId;
        this.usedButton = usedButton;
        this.clickedItem = ((clickedItem != null) ? clickedItem.copy() : null);
        this.actionNumber = actionNumber;
        this.mode = mode;
    }
    
    @Override
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processClickWindow(this);
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
        this.slotId = buf.readShort();
        this.usedButton = buf.readByte();
        this.actionNumber = buf.readShort();
        this.mode = buf.readByte();
        this.clickedItem = buf.readItemStackFromBuffer();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slotId);
        buf.writeByte(this.usedButton);
        buf.writeShort(this.actionNumber);
        buf.writeByte(this.mode);
        buf.writeItemStackToBuffer(this.clickedItem);
    }
    
    public int getWindowId() {
        return this.windowId;
    }
    
    public int getSlotId() {
        return this.slotId;
    }
    
    public int getUsedButton() {
        return this.usedButton;
    }
    
    public short getActionNumber() {
        return this.actionNumber;
    }
    
    public ItemStack getClickedItem() {
        return this.clickedItem;
    }
    
    public int getMode() {
        return this.mode;
    }
}
