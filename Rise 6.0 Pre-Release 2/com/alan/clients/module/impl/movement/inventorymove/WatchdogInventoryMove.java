package com.alan.clients.module.impl.movement.inventorymove;

import com.alan.clients.module.impl.movement.InventoryMove;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;
import net.minecraft.block.BlockChest;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

/**
 * @author Alan
 * @since 16.05.2022
 */

public class WatchdogInventoryMove extends Mode<InventoryMove> {

    private boolean inventoryOpen;

    public WatchdogInventoryMove(String name, InventoryMove parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer && inventoryOpen) {
            mc.thePlayer.motionX *= 0.2;
            mc.thePlayer.motionZ *= 0.2;
        } else if (packet instanceof C16PacketClientStatus) {
            final C16PacketClientStatus wrapper = (C16PacketClientStatus) packet;

            if (wrapper.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                inventoryOpen = true;
            }
        } else if (packet instanceof C0BPacketEntityAction) {
            final C0BPacketEntityAction wrapper = (C0BPacketEntityAction) packet;

            if (wrapper.getAction() == C0BPacketEntityAction.Action.OPEN_INVENTORY) {
                inventoryOpen = true;
            }
        } else if (packet instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = ((C08PacketPlayerBlockPlacement) packet);

            if (PlayerUtil.block(c08PacketPlayerBlockPlacement.getPosition()) instanceof BlockChest) {
                inventoryOpen = true;
            }
        } else if (packet instanceof C0DPacketCloseWindow) {
            inventoryOpen = false;
        } else if (packet instanceof C0EPacketClickWindow) {
            inventoryOpen = true;
        }
    };
}