package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.keybind.Keybind;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.KeybindSetting;
import me.jellysquid.mods.sodium.common.walden.util.AccessorUtils;
import me.jellysquid.mods.sodium.common.walden.util.InventoryUtils;

public class ATL extends Module implements PlayerTickListener
{
    private IntegerSetting minTotems;
    private IntegerSetting minPearls;
    private IntegerSetting dropInterval;
    private Keybind activateKeybind;
    private KeybindSetting activateKey;
    private int dropClock;

    public ATL() {
        super("AutoLooter Legit", "Helps you loot kills Credits to mera and zoops", false, Category.COMBAT);
        this.minTotems = new IntegerSetting.Builder().setName("Totems To Keep").setDescription("minimum totems to keep in your inventory").setModule(this).setValue(2).setMin(0).setMax(36).setAvailability(() -> true).build();
        this.minPearls = new IntegerSetting.Builder().setName("Pearls To Keep").setDescription("minimum pearls to keep in your inventory").setModule(this).setValue(16).setMin(0).setMax(576).setAvailability(() -> true).build();
        this.dropInterval = new IntegerSetting.Builder().setName("Dropping Speed").setDescription("the speed of dropping items").setModule(this).setValue(0).setMin(0).setMax(10).setAvailability(() -> true).build();
        this.activateKeybind = new Keybind("AutoLootYeeter_activateKeybind", 88, false, false, null);
        this.activateKey = new KeybindSetting.Builder().setName("Binding").setDescription("the key to activate it").setModule(this).setValue(this.activateKeybind).build();
        this.dropClock = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ATL.eventManager.add(PlayerTickListener.class, this);
        this.dropClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ATL.eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void onPlayerTick() {
        final PlayerInventory inv = ATL.mc.player.getInventory();
        if (this.dropClock != 0) {
            --this.dropClock;
            return;
        }
        if (!(mc.currentScreen instanceof InventoryScreen)) {
            return;
        }
        if (!this.activateKeybind.isDown()) {
            return;
        }
        final Screen screen = MinecraftClient.getInstance().currentScreen;
        final HandledScreen<?> gui = (HandledScreen<?>)screen;
        final Slot slot = AccessorUtils.getSlotUnderMouse(gui);
        if (slot == null) {
            return;
        }
        final int SlotUnderMouse = AccessorUtils.getSlotUnderMouse(gui).getIndex();
        if (SlotUnderMouse > 35) {
            return;
        }
        if (SlotUnderMouse < 9) {
            return;
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.TOTEM_OF_UNDYING) {
            if (InventoryUtils.countItem(Items.TOTEM_OF_UNDYING) <= this.minTotems.get()) {
                return;
            }
            ATL.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen) ATL.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity) ATL.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.ENDER_PEARL) {
            if (InventoryUtils.countItem(Items.ENDER_PEARL) <= this.minPearls.get()) {
                return;
            }
            ATL.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen) ATL.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity) ATL.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.DIRT) {
            ATL.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen) ATL.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity) ATL.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.COBBLESTONE) {
            ATL.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen) ATL.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity) ATL.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.GRASS_BLOCK) {
            ATL.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen) ATL.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity) ATL.mc.player);
            this.dropClock = this.dropInterval.get();
        }
    }
}
