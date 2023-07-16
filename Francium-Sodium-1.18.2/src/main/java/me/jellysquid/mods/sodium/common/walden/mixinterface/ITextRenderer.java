package me.jellysquid.mods.sodium.common.walden.mixinterface;

import net.minecraft.text.StringVisitable;
import net.minecraft.util.math.Matrix4f;

public interface ITextRenderer
{
	void drawTrimmed(StringVisitable text, float x, float y, int maxWidth, int color, Matrix4f matrix);
}
