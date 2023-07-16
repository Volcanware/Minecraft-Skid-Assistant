package me.jellysquid.mods.sodium.common.walden.keybind;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.event.events.KeyPressListener;
import me.jellysquid.mods.sodium.common.walden.gui.GuiScreen;
import me.jellysquid.mods.sodium.common.walden.module.modules.client.CGS;
import me.jellysquid.mods.sodium.common.walden.module.modules.client.SD;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class KeybindManager implements KeyPressListener
{

	private final ArrayList<Keybind> keybinds = new ArrayList<>();

	public KeybindManager()
	{
		ConfigManager.INSTANCE.getEventManager().add(KeyPressListener.class, this);
		addDefaultKeybinds();
	}

	public ArrayList<Keybind> getAllKeybinds()
	{
		return (ArrayList<Keybind>) keybinds.clone();
	}

	public void removeAll()
	{
		keybinds.clear();
	}

	public void addKeybind(Keybind keybind)
	{
		keybinds.add(keybind);
	}

	public void removeKeybind(Keybind keybind)
	{
		keybinds.remove(keybind);
	}

	public void removeKeybind(String name)
	{
		keybinds.removeIf(e -> e.getName().equals(name));
	}

	@Override
	public void onKeyPress(KeyPressListener.KeyPressEvent event)
	{
		for (Keybind keybind : keybinds)
		{
			if (event.getKeyCode() == keybind.getKey())
			{
				if (event.getAction() == GLFW.GLFW_PRESS)
					keybind.press();
				if (event.getAction() == GLFW.GLFW_RELEASE)
					keybind.release();
			}
		}
		//event.cancel();
	}

	public void addDefaultKeybinds()
	{
		addKeybind(new Keybind("gui", CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).activateKey.get().getKey(), true, false, () ->
		{
			if (SD.destruct) return;
			if (MC.currentScreen != null) return;
			MC.setScreen(new GuiScreen());
		}));
	}
}
