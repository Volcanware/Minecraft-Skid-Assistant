package me.jellysquid.mods.sodium.common.walden.mixin;

import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.event.events.GameRenderListener;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(
            at = {@At(value = "FIELD",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0)},
            method = {
                    "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"},
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        GameRenderListener.GameRenderEvent event = new GameRenderListener.GameRenderEvent(matrices, tickDelta);
        EventManager.fire(event);
    }
}