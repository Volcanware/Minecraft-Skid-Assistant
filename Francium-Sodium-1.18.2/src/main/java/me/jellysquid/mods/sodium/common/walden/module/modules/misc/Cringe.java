package me.jellysquid.mods.sodium.common.walden.module.modules.misc;

import me.jellysquid.mods.sodium.common.walden.event.events.SendChatMessageListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;

public class Cringe extends Module implements SendChatMessageListener
{
	public Cringe()
	{
		super("Auto Cringe", "cringe", false, Category.MISC);
	}

	@Override
	public void onEnable()
	{
		super.onEnable();
		eventManager.add(SendChatMessageListener.class, this);
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
		eventManager.remove(SendChatMessageListener.class, this);
	}

	@Override
	public void sendChatMessage(SendChatMessageEvent event)
	{
		char[] chars = event.getMessage().toCharArray();
		boolean bl = false;
		for (int i = 0; i < chars.length; i++)
		{
			if (bl)
				chars[i] = Character.toUpperCase(chars[i]);
			else
				chars[i] = Character.toLowerCase(chars[i]);
			bl = !bl;
		}
		event.setMessage(new String(chars));
	}
}
