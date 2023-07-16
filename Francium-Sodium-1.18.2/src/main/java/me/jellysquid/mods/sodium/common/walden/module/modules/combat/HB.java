package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.DecimalSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class HB extends Module {

    private DecimalSetting hitboxSize = DecimalSetting.Builder.newInstance()
            .setName("Expand")
            .setDescription("size of hitbox")
            .setModule(this)
            .setValue(0.5)
            .setMin(0.1)
            .setMax(5.0)
            .setAvailability(() -> true)
            .build();

    private BooleanSetting renderHB = BooleanSetting.Builder.newInstance()
            .setName("Render HB")
            .setDescription("render expanded HB")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    public HB() {
        super("Hitboxes", "expand players hitbox", false, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public float getHitboxSize(Entity entity) {
        if (this.isEnabled() && entity.getType() == EntityType.PLAYER) {
            return hitboxSize.get().floatValue();
        }

        return 0.0f;
    }

    public boolean shouldHitboxRender() {
        return renderHB.get();
    }


}
