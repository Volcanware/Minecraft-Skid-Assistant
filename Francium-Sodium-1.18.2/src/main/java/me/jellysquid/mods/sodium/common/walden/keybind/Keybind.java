package me.jellysquid.mods.sodium.common.walden.keybind;

public class Keybind
{

	private final String name;
	private int key;
	private final boolean activateOnPress;
	private final boolean activateOnRelease;
	private final Runnable action;

	private boolean down = false;

	public Keybind(String name, int key, boolean activateOnPress, boolean activateOnRelease, Runnable action)
	{
		this.name = name;
		this.key = key;
		this.activateOnPress = activateOnPress;
		this.activateOnRelease = activateOnRelease;
		this.action = action;
	}

	public String getName()
	{
		return name;
	}

	public int getKey()
	{
		return key;
	}

	public void setKey(int key)
	{
		this.key = key;
	}

	public void press()
	{
		down = true;
		if (activateOnPress)
			execute();
	}

	public void release()
	{
		down = false;
		if (activateOnRelease)
			execute();
	}

	public boolean isDown()
	{
		return down;
	}

	private void execute()
	{
		action.run();
	}
}