package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.keybind.Keybind;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.KeybindSetting;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class AITL extends Module implements PlayerTickListener {

    private final IntegerSetting delay = new IntegerSetting.Builder()
            .setName("Delay")
            .setDescription("the delay for auto switch after opening inventory")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(20)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting totemSlot = new IntegerSetting.Builder()
            .setName("Totem Slot")
            .setDescription("your totem slot")
            .setModule(this)
            .setValue(1)
            .setMin(1)
            .setMax(9)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting activateOnKey = new BooleanSetting.Builder()
            .setName("Activate On Key")
            .setDescription("whether or not to activate it only when pressing the selected key")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    public final KeybindSetting activateKey = new KeybindSetting.Builder()
            .setName("Keybind")
            .setDescription("the key to activate it")
            .setModule(this)
            .setValue(new Keybind("", GLFW.GLFW_KEY_V,false,false,null))
            .build();

    public AITL() {
        super("Legit Retotem", "Automatically puts on totems for you when you are in inventory", false, Category.COMBAT);
    }

    private int totemClock = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);

        totemClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }

    private boolean check() {
        return      (activateOnKey.get()
                && (GLFW.glfwGetKey(MC.getWindow().getHandle(),
                activateKey.get().getKey()) == GLFW.GLFW_PRESS))
                || (!activateOnKey.get());
    }

    @Override
    public void onPlayerTick() {

        if (MC.currentScreen instanceof InventoryScreen) {

            InventoryScreen invScreen = (InventoryScreen) MC.currentScreen;

            if (getFocusedSlot(invScreen) != null && check()) {

                int slot = getFocusedSlot(invScreen).getIndex();

                if (slot <= 35) {
                    if (!isTotem(totemSlot.get() - 1) && isTotem(slot)) {

                        if (totemClock != delay.get()) {
                            totemClock++;
                            return;
                        }

                        MC.interactionManager.clickSlot(
                                invScreen.getScreenHandler().syncId,
                                slot,
                                totemSlot.get() - 1,
                                SlotActionType.SWAP,
                                MC.player);
                        totemClock = 0;
                    }

                    if (!MC.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)
                            && isTotem(slot)) {

                        if (totemClock != delay.get()) {
                            totemClock++;
                            return;
                        }

                        MC.interactionManager.clickSlot(
                                invScreen.getScreenHandler().syncId,
                                slot,
                                40,
                                SlotActionType.SWAP,
                                MC.player);
                        totemClock = 0;
                    }
                }

            }
        } else {
            totemClock = 0;
        }

    }

    private Slot getFocusedSlot(InventoryScreen screen) {
        return screen.focusedSlot;
    }

    private boolean isTotem(int slot) {
        return MC.player.getInventory().main.get(slot).isOf(Items.TOTEM_OF_UNDYING);
    }
}