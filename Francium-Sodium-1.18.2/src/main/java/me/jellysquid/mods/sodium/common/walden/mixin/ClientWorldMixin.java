package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.event.events.EntityDespawnListener;
import me.jellysquid.mods.sodium.common.walden.event.events.EntitySpawnListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin
{

	@Inject(method = "addEntityPrivate", at = @At("TAIL"))
	private void onAddEntityPrivate(int id, Entity entity, CallbackInfo info) {
		if (entity != null)
			EventManager.fire(new EntitySpawnListener.EntitySpawnEvent(entity));
	}

	@Inject(method = "removeEntity", at = @At("TAIL"))
	private void onFinishRemovingEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo info) {
		Entity entity = MinecraftClient.getInstance().world.getEntityById(entityId);
		if (entity != null)
			EventManager.fire(new EntityDespawnListener.EntityDespawnEvent(entity));
	}

}
