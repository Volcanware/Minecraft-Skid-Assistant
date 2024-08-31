package me.jellysquid.mods.sodium.common.walden.module.modules.client;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;

public class SD extends Module {

    public SD() {
        super("Self Destruct", "Self Destruct", false, Category.CLIENT);
    }

    public static boolean destruct = false;


    @Override
    public void onEnable() {
        this.onDestruct();
    }

    public void onDestruct() {
        if (ConfigManager.MC.currentScreen != null && ConfigManager.MC.player != null) {
            ConfigManager.MC.player.closeScreen();
        }

        destruct = true;

        for (int k = 0; k < ConfigManager.INSTANCE.getModuleManager().getModules().size(); k++) {
            Module module = ConfigManager.INSTANCE.getModuleManager().getModules().get(k);
            ConfigManager.INSTANCE.getModuleManager().getModule(module.getClass()).onDisable();
            ConfigManager.INSTANCE.getModuleManager().getModules().remove(module);
        }

        ConfigManager.INSTANCE.getKeybindManager().removeAll();
        ConfigManager.INSTANCE.getModuleManager().removeModules();
        ConfigManager.INSTANCE.panic();
        System.gc();
    }

}
