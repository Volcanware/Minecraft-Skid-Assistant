package me.jellysquid.mods.sodium.common.walden.util;

public enum MathUtils
{
	;
	public static double roundToStep(double value, double step)
	{
		return step * Math.round(value / step);
	}
}
