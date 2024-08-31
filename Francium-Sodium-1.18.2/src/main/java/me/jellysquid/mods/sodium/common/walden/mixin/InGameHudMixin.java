package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.event.events.RenderHudListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
	@Inject(method = "render", at = @At("TAIL"))
	private void onRender(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {

		RenderHudListener.RenderHudEvent event = new RenderHudListener.RenderHudEvent(matrixStack, tickDelta);
		EventManager.fire(event);

	}
}
