package me.jellysquid.mods.sodium.common.walden.gui;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.text.Text;

import static net.minecraft.text.Text.of;

public class GuiScreen extends Screen {

    private final ClickGui gui;

    public GuiScreen() {
        super(of("gui"));
        gui = ConfigManager.INSTANCE.getClickGui();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        gui.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        gui.handleMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        gui.handleMouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        gui.handleMouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        gui.handleMouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        gui.handleMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
