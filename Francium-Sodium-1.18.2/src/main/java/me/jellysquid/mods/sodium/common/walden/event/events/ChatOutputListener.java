package me.jellysquid.mods.sodium.common.walden.event.events;

import me.jellysquid.mods.sodium.common.walden.event.CancellableEvent;
import me.jellysquid.mods.sodium.common.walden.event.Listener;

import java.util.ArrayList;

public interface ChatOutputListener extends Listener {
	void onSendMessage(ChatOutputEvent event);

	class ChatOutputEvent extends CancellableEvent<ChatOutputListener>
	{

		private final String originalMessage;
		private String message;

		public ChatOutputEvent(String message)
		{
			originalMessage = this.message = message;
		}

		public String getOriginalMessage()
		{
			return originalMessage;
		}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}

		public boolean isModified()
		{
			return !originalMessage.equals(message);
		}

		@Override
		public void fire(ArrayList<ChatOutputListener> listeners)
		{
			for (ChatOutputListener listener : listeners)
			{
				listener.onSendMessage(this);
				if (isCancelled())
					return;
			}
		}

		@Override
		public Class<ChatOutputListener> getListenerType()
		{
			return ChatOutputListener.class;
		}
	}
}
