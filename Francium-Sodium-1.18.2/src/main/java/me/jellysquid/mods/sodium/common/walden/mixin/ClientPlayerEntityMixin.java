package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin
{
	@Inject(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
			ordinal = 0), method = "tick()V")
	private void onTick(CallbackInfo ci)
	{
		EventManager.fire(new PlayerTickListener.PlayerTickEvent());
	}

}
