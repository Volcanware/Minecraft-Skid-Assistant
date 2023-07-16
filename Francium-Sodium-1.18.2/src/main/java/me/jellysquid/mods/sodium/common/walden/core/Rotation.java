package me.jellysquid.mods.sodium.common.walden.core;

public class Rotation
{
	private final float yaw;
	private final float pitch;

	private final boolean ignoreYaw;
	private final boolean ignorePitch;

	public Rotation(float yaw, float pitch)
	{
		this.yaw = yaw;
		this.pitch = pitch;
		ignoreYaw = false;
		ignorePitch = false;
	}

	public Rotation(float yaw, boolean ignoreYaw, float pitch, boolean ignorePitch)
	{
		this.yaw = yaw;
		this.ignoreYaw = ignoreYaw;
		this.pitch = pitch;
		this.ignorePitch = ignorePitch;
	}

	public float getYaw()
	{
		return yaw;
	}
	public float getPitch()
	{
		return pitch;
	}
	public boolean isIgnoreYaw()
	{
		return ignoreYaw;
	}
	public boolean isIgnorePitch()
	{
		return ignorePitch;
	}
}