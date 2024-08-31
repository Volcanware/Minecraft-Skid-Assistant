package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.event.events.AttackEntityListener;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class AWT extends Module implements AttackEntityListener, PlayerTickListener {

    private IntegerSetting delay = IntegerSetting.Builder.newInstance()
            .setName("Delay")
            .setDescription("delay in ticks")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(10)
            .setAvailability(() -> true)
            .build();

    private int delayClock = 0;
    private boolean reset = false;

    public AWT() {
        super("Auto WTap", "automaticly reset sprint", false, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(AttackEntityListener.class, this);
        eventManager.add(PlayerTickListener.class, this);

        reset = false;
        delayClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(AttackEntityListener.class, this);
        eventManager.remove(PlayerTickListener.class, this);;
    }

    @Override
    public void onPlayerTick() {
        if (reset && delayClock != delay.get())
            delayClock++;
        else if (reset) {
            MC.options.sprintKey.setPressed(true);
            reset = false;
            delayClock = 0;
        }
    }

    @Override
    public void onAttackEntity(AttackEntityEvent event) {
        if (MC.player.isSprinting()) {
            MC.options.sprintKey.setPressed(false);
            reset = true;
        }
    }
}