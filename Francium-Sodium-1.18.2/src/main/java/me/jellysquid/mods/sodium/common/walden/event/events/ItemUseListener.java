package me.jellysquid.mods.sodium.common.walden.event.events;

import me.jellysquid.mods.sodium.common.walden.event.CancellableEvent;
import me.jellysquid.mods.sodium.common.walden.event.Listener;

import java.util.ArrayList;

public interface ItemUseListener extends Listener
{
	void onItemUse(ItemUseEvent event);

	class ItemUseEvent extends CancellableEvent<ItemUseListener>
	{

		@Override
		public void fire(ArrayList<ItemUseListener> listeners)
		{
			listeners.forEach(e -> e.onItemUse(this));
		}

		@Override
		public Class<ItemUseListener> getListenerType()
		{
			return ItemUseListener.class;
		}
	}
}
