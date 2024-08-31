package me.jellysquid.mods.sodium.common.walden.module.setting;

import me.jellysquid.mods.sodium.common.walden.gui.component.Component;
import me.jellysquid.mods.sodium.common.walden.gui.window.Window;
import me.jellysquid.mods.sodium.common.walden.module.Module;

import java.io.Serializable;

public abstract class Setting<T> implements Serializable
{

	private final String name;
	private final String description;

	protected Setting(String name, String description, Module module)
	{
		this.name = name;
		this.description = description;
		module.addSetting(this);
	}

	public abstract T get();
	public abstract void set(T value);

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public abstract Component makeComponent(Window parent);

}
