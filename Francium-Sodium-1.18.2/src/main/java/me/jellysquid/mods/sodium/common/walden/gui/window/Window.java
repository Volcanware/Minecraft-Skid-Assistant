package me.jellysquid.mods.sodium.common.walden.gui.window;


import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.common.walden.gui.component.Component;
import me.jellysquid.mods.sodium.common.walden.module.modules.client.CGS;
import me.jellysquid.mods.sodium.common.walden.text.IFont;
import me.jellysquid.mods.sodium.common.walden.util.RenderUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import me.jellysquid.mods.sodium.common.walden.gui.ClickGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class Window {
    public final ClickGui parent;
    private double x, y;
    private double width, length;
    private double scrollAmount = 0;
    protected boolean minimized = false;
    protected ArrayList<Component> components = new ArrayList<>();
    private String title = "";
    private boolean isDraggable = true;
    private boolean draggable = true;
    public boolean closable = false;
    public boolean minimizable = true;
    private boolean resizable = true;
    private boolean pinnable = true;

    public Window(ClickGui parent, double x, double y, double width, int length) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.length = length;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double r = CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).getHudColorRed();
        double g = CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).getHudColorGreen();
        double b = CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).getHudColorBlue();

        TextRenderer textRenderer = MC.textRenderer;
        if(length==20){
            return;
        }
        if (!minimized) {
            RenderSystem.setShader(GameRenderer::getPositionShader);

            if (!closable) {
                RenderSystem.setShaderColor(0.05f, 0.05f, 0.05f, 0.3f);
                this.length = ConfigManager.INSTANCE.getModuleManager().getSizeOfModulesByCategory(title) * 16d + 19d;
            } else {
                RenderSystem.setShaderColor(0.05f, 0.05f, 0.05f, 0.3f);
            }

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            RenderUtils.drawQuad(x, y, x + width, y + length, matrices);
            for (Component component : components) {
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.lineWidth(1);
                component.render(matrices, mouseX, mouseY, delta);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
        if (draggable) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor((float) r, (float) g, (float) b, 1.0f);
            RenderUtils.drawQuad(x, y, x + width, y + 15, matrices);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
        }
        if (closable) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor(1.0f, 0.2f, 0.2f, 1.0f);
            double x = getX() + width - 12;
            double y = getY() + 2;
            RenderUtils.drawQuad(x, y, x + 10, y + 10, matrices);

            if (CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).customFont.get()) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                IFont.COMFORTAA.drawString(matrices, "x", (float) (x + 2f),  (float) (y - 1), new Color(255, 255, 255).getRGB(), false);
            } else {
                textRenderer.draw(matrices, "x", (float) (x + 2.5f), (float) y, 0xFFFFFFFF);
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);


        }
        if (minimizable) {
            double x = getX() + width - 12;
            double y = getY() + 2;

            textRenderer.draw(matrices, minimized ? "▼" : "▲", (float) (x + 4.0f), (float) (y + 2), 0xFFFFFFFF);
        }


        if (!minimized) {
            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor((float) r, (float) g, (float) b, 1.0f);
            RenderUtils.drawQuad(x, y, x + 1, y + length, matrices);
            RenderUtils.drawQuad(x, y + length, x + width, y + length + 1, matrices);
            RenderUtils.drawQuad(x + width - 1, y, x + width, y + length, matrices);
        }

        double textX = x;
        double textY = y + (width / 2) - 57;

        if (closable) {
            textY = y + 3.5f;
        }

        if (CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).customFont.get()) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            RenderSystem.setShaderColor((float) 1.0f, (float) 1.0f, (float) 1.0f, 1.0f);
            IFont.COMFORTAA.drawCenteredString(matrices, title, (double) (textX + getWidth() / 2), (double) textY, new Color(255, 255, 255).getRGB());
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
        } else {
            textY = y + 4;
            RenderUtils.typeCentered(matrices, title, (int) (textX + getWidth() / 2), (int) textY, 0xFFFFFF);
        }

    }

    public void onMouseMoved(double mouseX, double mouseY) {
        for (Component component : components) {
            component.onMouseMoved(mouseX, mouseY);
        }
    }

    public void onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (closable) {
                double x = getX() + width - 10;
                double y = getY();
                if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + 10, y + 10))
                    parent.close(this);
            }
            if (minimizable) {
                double x = getX() + width - 10;
                double y = getY();
                if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + 10, y + 10))
                    minimized = !minimized;
            }
        }
        if (!minimized && !canDrag(mouseX, mouseY)) {
            if (RenderUtils.isHoveringOver(mouseX, mouseY, x, draggable ? y + 10 : y, x + width, y + length)) {
                for (Component component : components) {
                    component.onMouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    public void onMouseScrolled(double mouseX, double mouseY, double amount) {
        if (minimized)
            return;
        if (!RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + width, y + length))
            return;
        for (Component component : components) {
            if (component.onMouseScrolled(mouseX, mouseY, amount))
                return;
        }

        scrollAmount += amount * 2;
        if (scrollAmount > 0)
            scrollAmount = 0;
        else {
            for (Component component : components) {
                component.setY(component.getY() + amount * 2);
            }
        }
    }

    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Component component : components) {
            if (component.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                return true;
        }
        return false;
    }

    public void onClose() {

    }

    public boolean canDrag(double mouseX, double mouseY) {
        if (!draggable)
            return false;
        return RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + width, y + 10);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public boolean isDraggable() {
        return isDraggable;
    }

    public void setIsDraggable(boolean isDraggable) {
        this.isDraggable = isDraggable;
    }

    public boolean isHoveringOver(double mouseX, double mouseY) {
        return minimized ? canDrag(mouseX, mouseY) : RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + width, y + length);
    }
}
