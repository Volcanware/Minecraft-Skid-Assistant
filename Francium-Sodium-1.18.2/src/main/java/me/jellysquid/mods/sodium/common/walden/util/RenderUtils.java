package me.jellysquid.mods.sodium.common.walden.util;

import me.jellysquid.mods.sodium.common.walden.mixinterface.ITextRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public enum RenderUtils
{
	;

	public static void fillBox(BufferBuilder bufferBuilder, Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
	}

	public static void drawSolidBox(Box bb, MatrixStack matrixStack)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
				VertexFormats.POSITION);
		fillBox(bufferBuilder, bb, matrixStack);
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	public static void fillOutlinedBox(BufferBuilder bufferBuilder, Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
	}

	public static void drawOutlinedBox(Box bb, MatrixStack matrixStack)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
				VertexFormats.POSITION);
		fillOutlinedBox(bufferBuilder, bb, matrixStack);
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	public static void drawQuad(float x1, float y1, float x2, float y2, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, x1, y2, 0).next();
		bufferBuilder.vertex(matrix, x2, y2, 0).next();
		bufferBuilder.vertex(matrix, x2, y1, 0).next();
		bufferBuilder.vertex(matrix, x1, y1, 0).next();

		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	public static void drawQuad(double x1, double y1, double x2, double y2, MatrixStack matrixStack)
	{
		drawQuad((float) x1, (float) y1, (float) x2, (float) y2, matrixStack);
	}

	public static void drawOutlinedQuad(float x1, float y1, float x2, float y2, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, x1, y2, 0).next();
		bufferBuilder.vertex(matrix, x2, y2, 0).next();
		bufferBuilder.vertex(matrix, x2, y1, 0).next();
		bufferBuilder.vertex(matrix, x2, y2, 0).next();
		bufferBuilder.vertex(matrix, x1, y1, 0).next();
		bufferBuilder.vertex(matrix, x1, y2, 0).next();
		bufferBuilder.vertex(matrix, x1, y1, 0).next();
		bufferBuilder.vertex(matrix, x2, y1, 0).next();

		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	public static void drawOutlinedQuad(double x1, double y1, double x2, double y2, MatrixStack matrixStack)
	{
		drawOutlinedQuad((float) x1, (float) y1, (float) x2, (float) y2, matrixStack);
	}

	public static boolean isHoveringOver(double mouseX, double mouseY, double x1, double y1, double x2, double y2)
	{
		return mouseX > Math.min(x1, x2) && mouseX < Math.max(x1, x2) && mouseY > Math.min(y1, y2) && mouseY < Math.max(y1, y2);
	}

	public static Vec3d getCameraPos()
	{
		return MC.getBlockEntityRenderDispatcher().camera.getPos();
	}

	public static BlockPos getCameraBlockPos()
	{
		return MC.getBlockEntityRenderDispatcher().camera.getBlockPos();
	}

	public static void applyRegionalRenderOffset(MatrixStack matrixStack)
	{
		Vec3d camPos = getCameraPos();
		BlockPos blockPos = getCameraBlockPos();

		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;

		matrixStack.translate(regionX - camPos.x, -camPos.y, regionZ - camPos.z);
	}

	public static void typeCentered(MatrixStack stack, String text, int centerX, int y, int color) {
		MC.textRenderer.drawWithShadow(stack, text, centerX - MC.textRenderer.getWidth(text) / 2, y, color);
	}

	public static void typeCenteredTrimmed(MatrixStack stack, String text, float centerX, float y, int maxWidth, int color) {
		ITextRenderer textRenderer = (ITextRenderer) MC.textRenderer;
		textRenderer.drawTrimmed(new LiteralText(text), centerX - MC.textRenderer.getWidth(text) / 2, y, maxWidth, color, stack.peek().getPositionMatrix());
	}

}
