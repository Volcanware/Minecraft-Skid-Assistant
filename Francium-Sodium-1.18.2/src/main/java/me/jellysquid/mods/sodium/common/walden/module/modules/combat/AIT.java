package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.keybind.Keybind;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.KeybindSetting;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class AIT extends Module implements PlayerTickListener {

    private final BooleanSetting autoSwitch = new BooleanSetting.Builder()
            .setName("autoSwitch")
            .setDescription("automatically switches to your totem slot")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting delay = new IntegerSetting.Builder()
            .setName("delay")
            .setDescription("the delay for auto switch after opening inventory")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(20)
            .setAvailability(autoSwitch::get)
            .build();

    private final IntegerSetting totemSlot = new IntegerSetting.Builder()
            .setName("totemSlot")
            .setDescription("your totem slot")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(8)
            .setAvailability(autoSwitch::get)
            .build();

    private final BooleanSetting forceTotem = new BooleanSetting.Builder()
            .setName("forceTotem")
            .setDescription("replace your main hand item (if there is one)")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting activateOnKey = new BooleanSetting.Builder()
            .setName("Activate On Key")
            .setDescription("whether or not to activate it only when pressing the selected key")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final Keybind activateKeybind = new Keybind(
            "AutoInventoryTotem_activateKeybind",
            GLFW.GLFW_KEY_C,
            false,
            false,
            null
    );

    private final KeybindSetting activateKey = new KeybindSetting.Builder()
            .setName("Activate Key")
            .setDescription("the key to activate it")
            .setModule(this)
            .setValue(activateKeybind)
            .build();

    public AIT() {
        super("Auto Retotem", "Automatically puts on totems for you when you are in inventory", false, Category.COMBAT);
    }

    private int invClock = -1;

    @Override
    public void onEnable() {
        super.onEnable();
        invClock = -1;
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void onPlayerTick() {
        if (!(mc.currentScreen instanceof InventoryScreen)) {
            invClock = -1;
            return;
        }
        if (invClock == -1)
            invClock = delay.get();
        if (invClock > 0) {
            invClock--;
            return;
        }
        PlayerInventory inv = mc.player.getInventory();
        if (autoSwitch.get())
            inv.selectedSlot = totemSlot.get();
        if (activateOnKey.get() && !activateKeybind.isDown())
            return;
        if (inv.offHand.get(0).getItem() != Items.TOTEM_OF_UNDYING) {
            int slot = findTotemSlot();
            if (slot != -1) {
                mc.interactionManager.clickSlot(((InventoryScreen) mc.currentScreen).getScreenHandler().syncId, slot, 40, SlotActionType.SWAP, mc.player);
                return;
            }
        }
        ItemStack mainHand = inv.main.get(inv.selectedSlot);
        if (mainHand.isEmpty() ||
                forceTotem.get() && mainHand.getItem() != Items.TOTEM_OF_UNDYING) {
            int slot = findTotemSlot();
            if (slot != -1) {
                mc.interactionManager.clickSlot(((InventoryScreen) mc.currentScreen).getScreenHandler().syncId, slot, inv.selectedSlot, SlotActionType.SWAP, mc.player);
            }
        }
    }

    private int findTotemSlot() {
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 9; i < 36; i++) {
            if (inv.main.get(i).getItem() == Items.TOTEM_OF_UNDYING)
                return i;
        }
        return -1;
    }
}