package me.jellysquid.mods.sodium.common.walden.mixin;

import me.jellysquid.mods.sodium.common.walden.mixinterface.ITextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(TextRenderer.class)
public class TextRendererMixin implements ITextRenderer
{
	@Shadow
	public List<OrderedText> wrapLines(StringVisitable text, int width)
	{
		return null;
	}

	@Shadow
	private int draw(OrderedText text, float x, float y, int color, Matrix4f matrix, boolean shadow)
	{
		return 0;
	}

	@Override
	public void drawTrimmed(StringVisitable text, float x, float y, int maxWidth, int color, Matrix4f matrix)
	{
		for(Iterator var7 = this.wrapLines(text, maxWidth).iterator(); var7.hasNext(); y += 9) {
			OrderedText orderedText = (OrderedText)var7.next();
			this.draw(orderedText, x, y, color, matrix, false);
		}
	}
}
