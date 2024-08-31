package me.jellysquid.mods.sodium.common.walden.module.modules.client;

import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.keybind.Keybind;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.KeybindSetting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class CGS extends Module implements PlayerTickListener {
    private final IntegerSetting hudColorRed = IntegerSetting.Builder.newInstance()
            .setName("red")
            .setDescription("hud color red")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();
    private final IntegerSetting hudColorGreen = IntegerSetting.Builder.newInstance()
            .setName("green")
            .setDescription("hud color green")
            .setModule(this)
            .setValue(90)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();
    private final IntegerSetting hudColorBlue = IntegerSetting.Builder.newInstance()
            .setName("blue")
            .setDescription("hud color blue")
            .setModule(this)
            .setValue(180)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();
    public final BooleanSetting customFont = BooleanSetting.Builder.newInstance()
            .setName("Custom Font")
            .setDescription("custom font")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();
    private final BooleanSetting rgbEffect = BooleanSetting.Builder.newInstance()
            .setName("Breathing")
            .setDescription("Setting to make funny gayming rgb")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();
    public final KeybindSetting activateKey = new KeybindSetting.Builder()
            .setName("Keybind")
            .setDescription("the key to activate it")
            .setModule(this)
            .setValue(new Keybind("", GLFW.GLFW_KEY_RIGHT_CONTROL,true,false,null))
            .build();

    public double h = 360;
    public double s = 1;
    public double v = 1;

    public CGS(){
        super("Click Gui", "modify the gui", true, Category.CLIENT);
        eventManager.add(PlayerTickListener.class,this);
    }

    @Override
    public void onEnable() {
        super.onEnable();

    }

    @Override
    public void onDisable() {
        this.setEnabled(true);
    }

    public int getRed() {
        return hudColorRed.get();
    }

    public int getGreen() {
        return hudColorGreen.get();
    }

    public int getBlue() {
        return hudColorBlue.get();
    }

    public double getHudColorBlue() {
        if(rgbEffect.get()) {
            Color rgb = new Color(Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return rgb.getBlue()/255.0;
        }
        return hudColorBlue.get()/255.0;
    }

    public double getHudColorGreen() {
        if(rgbEffect.get()) {
            int rgb = (Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return new Color(rgb).getGreen()/255.0;
        }
        return hudColorGreen.get()/255.0;
    }

    public double getHudColorRed() {
        if(rgbEffect.get()) {
            Color rgb = new Color(Color.HSBtoRGB((float) h / 360.0f, (float) s, (float) v));
            return rgb.getRed() / 255.0;
        }
        return hudColorRed.get() / 255.0;
    }

    @Override
    public void onPlayerTick() {

        if (h < 360) {
            h++;
        } else {
            h = 0;
        }

    }
}
