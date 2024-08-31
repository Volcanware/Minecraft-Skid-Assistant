package me.jellysquid.mods.sodium.common.walden.event.events;

import me.jellysquid.mods.sodium.common.walden.event.CancellableEvent;
import me.jellysquid.mods.sodium.common.walden.event.Listener;

import java.util.ArrayList;

public interface KeyPressListener extends Listener
{
	void onKeyPress(KeyPressEvent event);

	class KeyPressEvent extends CancellableEvent<KeyPressListener>
	{

		private int keyCode, scanCode, action, modifiers;

		public KeyPressEvent(int keyCode, int scanCode, int action, int modifiers)
		{
			this.keyCode = keyCode;
			this.scanCode = scanCode;
			this.action = action;
			this.modifiers = modifiers;
		}

		@Override
		public void fire(ArrayList<KeyPressListener> listeners)
		{
			listeners.forEach(e -> e.onKeyPress(this));
		}

		public int getKeyCode()
		{
			return keyCode;
		}

		public int getScanCode()
		{
			return scanCode;
		}

		public int getAction()
		{
			return action;
		}

		public int getModifiers()
		{
			return modifiers;
		}

		@Override
		public Class<KeyPressListener> getListenerType()
		{
			return KeyPressListener.class;
		}
	}
}
