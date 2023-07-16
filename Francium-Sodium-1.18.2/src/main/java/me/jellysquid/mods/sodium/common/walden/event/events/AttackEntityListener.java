package me.jellysquid.mods.sodium.common.walden.event.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import me.jellysquid.mods.sodium.common.walden.event.CancellableEvent;
import me.jellysquid.mods.sodium.common.walden.event.Listener;

import java.util.ArrayList;

public interface AttackEntityListener extends Listener
{
	void onAttackEntity(AttackEntityEvent event);

	class AttackEntityEvent extends CancellableEvent<AttackEntityListener>
	{
		private final PlayerEntity player;
		private final Entity target;

		public AttackEntityEvent(PlayerEntity player, Entity target)
		{
			this.player = player;
			this.target = target;
		}

		public PlayerEntity getPlayer()
		{
			return player;
		}

		public Entity getTarget()
		{
			return target;
		}

		@Override
		public void fire(ArrayList<AttackEntityListener> listeners)
		{
			for (AttackEntityListener listener : listeners)
			{
				listener.onAttackEntity(this);
			}
		}

		@Override
		public Class<AttackEntityListener> getListenerType()
		{
			return AttackEntityListener.class;
		}
	}
}
