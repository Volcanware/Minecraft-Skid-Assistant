package dev.client.tenacity.ui.mainmenu;

import dev.client.tenacity.ui.Screen;
import dev.client.tenacity.utils.misc.HoveringUtil;
import dev.client.tenacity.utils.render.ColorUtil;
import dev.client.tenacity.utils.render.RenderUtil;
import dev.utils.animations.Animation;
import dev.utils.animations.Direction;
import dev.utils.animations.impl.EaseInOutQuad;
import dev.utils.font.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class MenuButton
implements Screen {
    public final String location;
    public final String text;
    private Animation hoverAnimation;
    public float x;
    public float y;
    public float buttonWH;
    public Runnable clickAction;

    public MenuButton(String location, String text) {
        this.location = location;
        this.text = text;
    }

    @Override
    public void initGui() {
        this.hoverAnimation = new EaseInOutQuad(200, 1.0);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        boolean hovered = HoveringUtil.isHovering(this.x, this.y, this.buttonWH, this.buttonWH, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        RenderUtil.setAlphaLimit(0.0f);
        mc.getTextureManager().bindTexture(new ResourceLocation(this.location));
        Gui.drawModalRectWithCustomSizedTexture(this.x, (float)((double)this.y - 15.0 * this.hoverAnimation.getOutput()), 0.0f, 0.0f, this.buttonWH, this.buttonWH, this.buttonWH, this.buttonWH);
        RenderUtil.setAlphaLimit(0.0f);
        RenderUtil.resetColor();
        FontUtil.tenacityFont22.drawCenteredString(this.text, this.x + this.buttonWH / 2.0f, (float)((double)(this.y + this.buttonWH - 20.0f) + 8.0 * this.hoverAnimation.getOutput()), ColorUtil.applyOpacity(-1, (float)this.hoverAnimation.getOutput()));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = HoveringUtil.isHovering(this.x, this.y, this.buttonWH, this.buttonWH, mouseX, mouseY);
        if (!hovered) return;
        this.clickAction.run();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}