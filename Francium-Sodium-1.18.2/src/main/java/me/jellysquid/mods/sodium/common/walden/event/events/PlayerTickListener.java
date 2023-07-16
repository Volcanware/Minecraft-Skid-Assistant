package me.jellysquid.mods.sodium.common.walden.event.events;

import me.jellysquid.mods.sodium.common.walden.event.Event;
import me.jellysquid.mods.sodium.common.walden.event.Listener;

import java.util.ArrayList;

public interface PlayerTickListener extends Listener
{
	void onPlayerTick();

	class PlayerTickEvent extends Event<PlayerTickListener>
	{

		@Override
		public void fire(ArrayList<PlayerTickListener> listeners)
		{
			listeners.forEach(PlayerTickListener::onPlayerTick);
		}

		@Override
		public Class<PlayerTickListener> getListenerType()
		{
			return PlayerTickListener.class;
		}
	}
}
