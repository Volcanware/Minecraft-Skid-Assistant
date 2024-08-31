package me.jellysquid.mods.sodium.common.walden.gui.component;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.modules.client.CGS;
import me.jellysquid.mods.sodium.common.walden.text.IFont;
import me.jellysquid.mods.sodium.common.walden.util.RenderUtils;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.text.Text;
import me.jellysquid.mods.sodium.common.walden.gui.ClickGui;
import me.jellysquid.mods.sodium.common.walden.gui.window.ModuleSettingWindow;
import me.jellysquid.mods.sodium.common.walden.gui.window.Window;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class ModuleButtonComponent extends Component {

    private final Module module;
    private boolean settingWindowOpened = false;
    private ModuleSettingWindow moduleSettingWindow;

    public ModuleButtonComponent(Window parent, Module module, double x, double y) {
        super(parent, x, y, 10, module.getName());
        this.module = module;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double r = CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).getHudColorRed();
        double g = CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).getHudColorGreen();
        double b = CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).getHudColorBlue();
        double parentX = parent.getX();
        double parentY = parent.getY();
        double parentWidth = parent.getWidth();
        double parentLength = parent.getLength();
        double parentX2 = parent.getX() + parentWidth;
        double parentY2 = parent.getY() + parentLength;
        double x = getX() + parentX;
        double y = Math.max(getY() + parentY, parentY);
        double x2 = parentX2 - getX();
        double y2 = Math.min(getY() + parentY + 16, parentY2);
        if (getY() + 16 <= 0)
            return;
        if (parentY2 - (getY() + parentY) <= 0)
            return;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2)) {
            if (module.isEnabled())
                RenderSystem.setShaderColor((float) r, (float) g, (float) b, 0.6f);
            else
                RenderSystem.setShaderColor((float) r, (float) g, (float) b, 0.2f);
        } else {
            if (module.isEnabled())
                RenderSystem.setShaderColor((float) r, (float) g, (float) b, 0.4f);
            else
                RenderSystem.setShaderColor(0.05f, 0.05f, 0.05f, 0.3f);
        }
        RenderUtils.drawQuad(x + 3, y, x2 - 3, y2, matrices);


        if (module.isEnabled()) {
            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor((float) r, (float) g, (float) b, 0.8f);
            RenderUtils.drawQuad(x2 - 5, y, x2 - 3, y2, matrices);
        }

        double textX = x;
        double textY = y + ((y2 - y) / 2) - 5;

        if (CGS.class.cast(ConfigManager.INSTANCE.getModuleManager().getModule(CGS.class)).customFont.get()) {
            RenderSystem.setShaderColor((float) 1.0f, (float) 1.0f, (float) 1.0f, 1.0f);
            IFont.COMFORTAA.drawCenteredString(matrices, module.getName(), (double) (textX + parentWidth / 2), (double) textY, new Color(255, 255, 255).getRGB());
        } else {
            textY = y + MC.textRenderer.getWrappedLinesHeight(module.getName(), MC.textRenderer.getWidth(module.getName())) / 2;
            RenderUtils.typeCenteredTrimmed(matrices, module.getName(), (float) (textX + parentWidth / 2), (float) textY, (int) (x2 - textX), 0xFFFFFF);
        }

    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int button) {
        double parentX = parent.getX();
        double parentY = parent.getY();
        double parentWidth = parent.getWidth();
        double parentLength = parent.getLength();
        double parentX2 = parent.getX() + parentWidth;
        double parentY2 = parent.getY() + parentLength;
        double x = getX() + parentX;
        double y = getY() + parentY;
        double x2 = parentX2 - getX();
        double y2 = Math.min(y + 16, parentY2);
        if (getY() + 16 <= 0)
            return;
        if (parentY2 - (getY() + parentY) <= 0)
            return;
        if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                module.toggle();
            } else {
                if (!settingWindowOpened && module.getSettings().size()!=0) {
                    ClickGui gui = parent.parent;
                    moduleSettingWindow = new ModuleSettingWindow(gui, mouseX, mouseY, module, this);
                    gui.add(moduleSettingWindow);
                    settingWindowOpened = true;
                } else if(module.getSettings().size()!=0) {
                    parent.parent.moveToTop(moduleSettingWindow);
                }
            }
        }
    }

    public void settingWindowClosed() {
        settingWindowOpened = false;
        moduleSettingWindow = null;
    }
}
