package me.jellysquid.mods.sodium.common.walden;

import me.jellysquid.mods.sodium.common.walden.mixin.MinecraftClientAccessor;
import me.jellysquid.mods.sodium.common.walden.util.*;
import me.jellysquid.mods.sodium.common.walden.core.CrystalDataTracker;
import me.jellysquid.mods.sodium.common.walden.core.PlayerActionScheduler;
import me.jellysquid.mods.sodium.common.walden.core.Rotator;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.gui.ClickGui;
import me.jellysquid.mods.sodium.common.walden.keybind.KeybindManager;
import me.jellysquid.mods.sodium.common.walden.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

public enum ConfigManager
{

    INSTANCE;

    public static MinecraftClient MC;

    private EventManager eventManager;
    private ModuleManager moduleManager;
    private KeybindManager keybindManager;
    private ClickGui gui;
    private Boolean guiInitialized = false;
    private CrystalDataTracker crystalDataTracker;
    private PlayerActionScheduler playerActionScheduler;
    private Rotator rotator;

    public void init() {
        MC = MinecraftClient.getInstance();
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        keybindManager = new KeybindManager();
        gui = new ClickGui();
        crystalDataTracker = new CrystalDataTracker();
        playerActionScheduler = new PlayerActionScheduler();
        rotator = new Rotator();
    }

    public void panic() {
        guiInitialized = null;
        MC = null;
        eventManager = null;
        moduleManager = null;
        keybindManager = null;
        gui = null;
        crystalDataTracker = null;
        playerActionScheduler = null;
        rotator = null;
    }

    public EventManager getEventManager() { return eventManager; }

    public ModuleManager getModuleManager()
    {
        return moduleManager;
    }

    public KeybindManager getKeybindManager()
    {
        return keybindManager;
    }

    public ClickGui getClickGui() {
        if (!guiInitialized) {
            gui.init();
            guiInitialized = true;
        }

        return gui;
    }

    public void updateClickGui() {
        this.gui = new ClickGui();

        if (!guiInitialized) {
            gui.init();
            guiInitialized = true;
        }
    }

    public CrystalDataTracker getCrystalDataTracker() {
        return crystalDataTracker;
    }

    public PlayerActionScheduler getPlayerActionScheduler() {
        return playerActionScheduler;
    }

    public Rotator getRotator() {
        return rotator;

    }

}