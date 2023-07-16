package me.jellysquid.mods.sodium.common.walden.mixin;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.mixinterface.IBox;
import me.jellysquid.mods.sodium.common.walden.module.modules.client.SD;
import me.jellysquid.mods.sodium.common.walden.module.modules.combat.HB;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onRenderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo info, Box box) {
        if (!SD.destruct) {
            float size = HB.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(HB.class)).getHitboxSize(entity);
            boolean shouldRender = HB.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(HB.class)).shouldHitboxRender();

            if (size != 0 && shouldRender)
                ((IBox) box).expand(size);
        }

    }

}
