package me.jellysquid.mods.sodium.common.walden.mixin;


import me.jellysquid.mods.sodium.common.walden.util.CrystalUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalEntityMixin extends Entity {

    public EndCrystalEntityMixin(EntityType<?> type, World world) {
        super(null, null);
    }

    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true)
    private void onCrystalDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        if (CrystalUtils.explodesClientSide(source)) {
            remove(RemovalReason.KILLED);
            ci.setReturnValue(true);
        }
    }

}
