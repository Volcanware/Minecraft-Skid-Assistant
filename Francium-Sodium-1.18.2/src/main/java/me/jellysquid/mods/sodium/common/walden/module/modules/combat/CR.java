package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;

public class CR extends Module {

    public CR() {
        super("Crystal Remover", "haram client crystal remover", false, Category.COMBAT);
    }

    public static boolean removeCrystal;

    @Override
    public void onEnable() {
        super.onEnable();
        removeCrystal = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        removeCrystal = false;
    }

}
