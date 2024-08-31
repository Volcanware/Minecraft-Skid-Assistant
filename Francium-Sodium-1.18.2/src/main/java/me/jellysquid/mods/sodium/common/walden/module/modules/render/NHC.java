package me.jellysquid.mods.sodium.common.walden.module.modules.render;

import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;

public class NHC extends Module {

    public NHC() {
        super("No Hurt Cam", "No Hurt Cam", false, Category.RENDER);
    }

    public static boolean doHurtCam = true;

    @Override
    public void onEnable() {
        super.onEnable();
        doHurtCam = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        doHurtCam = true;
    }

}
