package me.jellysquid.mods.sodium.common.walden.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.mixinterface.ITextRenderer;
import me.jellysquid.mods.sodium.common.walden.module.modules.client.CGS;
import me.jellysquid.mods.sodium.common.walden.text.IFont;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import me.jellysquid.mods.sodium.common.walden.gui.window.Window;

import java.awt.*;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public abstract class Component {

    public final Window parent;
    private double x, y;
    private final double length;
    private final String name;

    public Component(Window parent, double x, double y, double length, String name) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.length = length;
        this.name = name;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        double parentX = parent.getX();
        double parentY = parent.getY();
        double parentWidth = parent.getWidth();
        double parentLength = parent.getLength();
        double parentX2 = parent.getX() + parentWidth;
        double parentY2 = parent.getY() + parentLength;
        double x = getX() + parentX;
        double y = getY() + parentY - 10;
        if (getY() + parentY - parentY - 10 <= 0)
            return;
        if (parentY2 - (getY() + parentY) <= 0)
            return;

        if (CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).customFont.get()) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            IFont.COMFORTAA.drawString(matrices, name, (float) x, (float) y, new Color(255, 255, 255).getRGB(), false);
        } else {
            ((ITextRenderer) MC.textRenderer).drawTrimmed(Text.of(name), (float) x, (float) y, (int) (parentX2 - x), 0xFFFFFF, matrices.peek().getPositionMatrix());
        }

    }

    public void onMouseMoved(double mouseX, double mouseY) {

    }

    public void onMouseClicked(double mouseX, double mouseY, int button) {

    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
