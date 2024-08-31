package me.jellysquid.mods.sodium.common.walden.module.setting;

import me.jellysquid.mods.sodium.common.walden.gui.component.Component;
import me.jellysquid.mods.sodium.common.walden.gui.component.SliderComponent;
import me.jellysquid.mods.sodium.common.walden.gui.window.Window;
import me.jellysquid.mods.sodium.common.walden.module.Module;

import java.util.function.Supplier;

public class DecimalSetting extends Setting<Double>
{

	private double value;
	private final double min;
	private final double max;
	private final double step;
	private final Supplier<Boolean> availability;

	private DecimalSetting(Builder builder)
	{
		super(builder.name, builder.description, builder.module);
		this.value = builder.value;
		this.min = builder.min;
		this.max = builder.max;
		this.step = builder.step;
		this.availability = builder.availability;
	}

	@Override
	public Double get()
	{
		return value;
	}

	@Override
	public void set(Double value)
	{
		this.value = value;
	}

	@Override
	public Component makeComponent(Window parent)
	{
		return new SliderComponent(parent, 0, 0, 60, value, min, max, step, SliderComponent.DisplayType.DECIMAL, v -> value = v, availability, getName());
	}

	public static class Builder
	{
		private String name;
		private String description;
		private Module module;
		private double value;
		private double min;
		private double max;
		private double step = 0.1;
		private Supplier<Boolean> availability = () -> true;

		public static Builder newInstance()
		{
			return new Builder();
		}

		public DecimalSetting build()
		{
			return new DecimalSetting(this);
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

		public Builder setValue(double value)
		{
			this.value = value;
			return this;
		}

		public Builder setMin(double min)
		{
			this.min = min;
			return this;
		}

		public Builder setMax(double max)
		{
			this.max = max;
			return this;
		}

		public Builder setStep(double step)
		{
			this.step = step;
			return this;
		}

		public Builder setAvailability(Supplier<Boolean> availability)
		{
			this.availability = availability;
			return this;
		}
	}
}
