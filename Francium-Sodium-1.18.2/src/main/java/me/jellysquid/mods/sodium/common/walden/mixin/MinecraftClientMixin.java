package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.MinecraftClient;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.event.events.ItemUseListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
	@Inject(at = @At("HEAD"), method = "doItemUse", cancellable = true)
	private void onDoItemUse(CallbackInfo ci)
	{
		ItemUseListener.ItemUseEvent event = new ItemUseListener.ItemUseEvent();
		EventManager.fire(event);
		if (event.isCancelled())
			ci.cancel();
	}
}
