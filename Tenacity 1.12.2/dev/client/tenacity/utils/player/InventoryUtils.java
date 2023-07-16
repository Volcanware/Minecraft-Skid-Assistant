package dev.client.tenacity.utils.player;

import dev.utils.Utils;
import net.minecraft.inventory.ClickType;

public class InventoryUtils implements Utils {

    public static void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, mouseButton, shiftClick ? ClickType.PICKUP_ALL : ClickType.PICKUP, mc.player);
    }

    public static void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, ClickType.THROW, mc.player);
    }

    public static void swap(int slot, int hSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, hSlot, ClickType.SWAP, mc.player);
    }

}
