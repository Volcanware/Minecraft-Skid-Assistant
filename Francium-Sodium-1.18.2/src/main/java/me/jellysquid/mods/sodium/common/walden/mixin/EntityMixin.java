package me.jellysquid.mods.sodium.common.walden.mixin;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.module.modules.client.SD;
import me.jellysquid.mods.sodium.common.walden.module.modules.combat.HB;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void onGetTargetingMargin(CallbackInfoReturnable<Float> info) {

        if (!SD.destruct) {
            float size = HB.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(HB.class)).getHitboxSize((Entity) (Object) this);

            if (size != 0) {
                info.setReturnValue(size);
            }
        }

    }

}
