package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import me.jellysquid.mods.sodium.common.walden.event.events.AttackEntityListener.AttackEntityEvent;
import me.jellysquid.mods.sodium.common.walden.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin
{
	@Inject(at = @At("HEAD"), method = "attackEntity", cancellable = true)
	private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci)
	{
		MixinUtils.fireCancellable(new AttackEntityEvent(player, target), ci);
	}
}
