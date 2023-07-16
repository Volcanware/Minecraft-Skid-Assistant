package me.jellysquid.mods.sodium.common.walden.module.setting;

import me.jellysquid.mods.sodium.common.walden.gui.component.Component;
import me.jellysquid.mods.sodium.common.walden.gui.component.SliderComponent;
import me.jellysquid.mods.sodium.common.walden.gui.window.Window;
import me.jellysquid.mods.sodium.common.walden.module.Module;

import java.util.function.Supplier;

public class IntegerSetting extends Setting<Integer>
{

	private int value;
	private final int min;
	private final int max;
	private final Supplier<Boolean> availability;

	private IntegerSetting(Builder builder)
	{
		super(builder.name, builder.description, builder.module);
		this.value = builder.value;
		this.min = builder.min;
		this.max = builder.max;
		this.availability = builder.availability;
	}

	@Override
	public Integer get()
	{
		return value;
	}

	@Override
	public void set(Integer value)
	{
		this.value = value;
	}

	@Override
	public Component makeComponent(Window parent)
	{
		return new SliderComponent(parent, 0, 0, 60, value, min, max, 1, SliderComponent.DisplayType.INTEGER, v -> value = v.intValue(), availability, getName());
	}

	public int getMin()
	{
		return min;
	}

	public int getMax()
	{
		return max;
	}

	public static class Builder
	{
		private String name;
		private String description;
		private Module module;
		private int value;
		private int min;
		private int max;
		private Supplier<Boolean> availability = () -> true;

		public static Builder newInstance()
		{
			return new Builder();
		}

		public IntegerSetting build()
		{
			return new IntegerSetting(this);
		}

		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}

		public Builder setDescription(String description)
		{
			this.description = description;
			return this;
		}

		public Builder setModule(Module module)
		{
			this.module = module;
			return this;
		}

		public Builder setValue(int value)
		{
			this.value = value;
			return this;
		}

		public Builder setMin(int min)
		{
			this.min = min;
			return this;
		}

		public Builder setMax(int max)
		{
			this.max = max;
			return this;
		}

		public Builder setAvailability(Supplier<Boolean> availability)
		{
			this.availability = availability;
			return this;
		}
	}
}
