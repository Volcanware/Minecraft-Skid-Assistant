package dev.client.tenacity.ui.clickguis.dropdown;

import dev.client.tenacity.Tenacity;
import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.module.impl.render.ClickGuiMod;
import dev.client.tenacity.ui.Screen;
import dev.client.tenacity.ui.clickguis.dropdown.impl.ModuleRect;
import dev.client.tenacity.utils.misc.HoveringUtil;
import dev.client.tenacity.utils.render.RenderUtil;
import dev.utils.animations.Animation;
import dev.utils.animations.Direction;
import dev.utils.animations.impl.DecelerateAnimation;
import dev.utils.font.FontUtil;
import dev.utils.misc.MathUtils;
import dev.utils.render.StencilUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class MainScreen
implements Screen {
    private final Category category;
    private final float rectWidth = 110.0f;
    private final float categoryRectHeight = 18.0f;
    public Animation animation;
    public HashMap<ModuleRect, Animation> moduleAnimMap = new HashMap();
    public Animation openingAnimation;
    private List<ModuleRect> moduleRects;

    public MainScreen(Category category) {
        this.category = category;
    }

    @Override
    public void initGui() {
        if (this.moduleRects == null) {
            this.moduleRects = new ArrayList<ModuleRect>();
            for (Module module : Tenacity.INSTANCE.getModuleCollection().getModulesInCategory(this.category).stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
                ModuleRect moduleRect = new ModuleRect(module);
                this.moduleRects.add(moduleRect);
                this.moduleAnimMap.put(moduleRect, new DecelerateAnimation(250, 1.0));
            }
        }
        if (this.moduleRects == null) return;
        this.moduleRects.forEach(ModuleRect::initGui);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.moduleRects == null) return;
        this.moduleRects.forEach(moduleRect -> moduleRect.keyTyped(typedChar, keyCode));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float animClamp = (float)Math.max(0.0, Math.min(255.0, 255.0 * this.animation.getOutput()));
        int alphaAnimation = (int)animClamp;
        int categoryRectColor = new Color(29, 29, 29, alphaAnimation).getRGB();
        int textColor = new Color(255, 255, 255, alphaAnimation).getRGB();
        this.category.getDrag().onDraw(mouseX, mouseY);
        float x = this.category.getDrag().getX();
        float y = this.category.getDrag().getY();
        Gui.drawRect2(x, y, 110.0, 18.0, categoryRectColor);
        RenderUtil.setAlphaLimit(0.0f);
        FontUtil.tenacityBoldFont26.drawString(this.category.name, x + 5.0f, y + FontUtil.tenacityBoldFont26.getMiddleOfBox(18.0f), textColor);
        String icon = this.category.icon;
        RenderUtil.setAlphaLimit(0.0f);
        RenderUtil.resetColor();
        FontUtil.iconFont20.drawString(icon, (double)(x + 110.0f) - (FontUtil.iconFont20.getStringWidth(icon) + 5.0), y + FontUtil.iconFont20.getMiddleOfBox(18.0f), textColor);
        ClickGuiMod clickGUIMod = (ClickGuiMod)Tenacity.INSTANCE.getModuleCollection().get(ClickGuiMod.class);
        if (ClickGuiMod.scrollMode.getMode().equals("Value")) {
            Module.allowedClickGuiHeight = ClickGuiMod.clickHeight.getValue().floatValue();
        } else {
            ScaledResolution sr = new ScaledResolution(mc);
            Module.allowedClickGuiHeight = (float)(2 * sr.getScaledHeight()) / 3.0f;
        }
        float allowedHeight = Module.allowedClickGuiHeight;
        boolean hoveringMods = HoveringUtil.isHovering(x, y + 18.0f, 110.0f, allowedHeight, mouseX, mouseY);
        float scaleAnim = Math.max(1.0f, (float)this.openingAnimation.getOutput() + 0.7f);
        float width = 110.0f;
        StencilUtil.initStencilToWrite();
        Gui.drawRect2(x - 100.0f, y + 18.0f, 260.0, allowedHeight, -1);
        StencilUtil.readStencilBuffer(1);
        double scroll = this.category.getScroll().getScroll();
        double count = 0.0;
        for (ModuleRect moduleRect : this.moduleRects) {
            Animation animation = this.moduleAnimMap.get(moduleRect);
            animation.setDirection(moduleRect.module.expanded ? Direction.FORWARDS : Direction.BACKWARDS);
            moduleRect.settingAnimation = animation;
            moduleRect.alphaAnimation = alphaAnimation;
            moduleRect.x = x;
            moduleRect.height = 17.0f;
            moduleRect.panelLimitY = y;
            moduleRect.openingAnimation = this.openingAnimation;
            moduleRect.y = (float)((double)(y + 18.0f) + count * 17.0 + MathUtils.roundToHalf(scroll));
            moduleRect.width = 110.0f;
            moduleRect.drawScreen(mouseX, mouseY);
            count += 1.0 + moduleRect.getSettingSize();
        }
        if (hoveringMods) {
            this.category.getScroll().onScroll(30);
            float hiddenHeight = (float)(count * 17.0 - (double)allowedHeight);
            this.category.getScroll().setMaxScroll(Math.max(0.0f, hiddenHeight));
        }
        StencilUtil.uninitStencilBuffer();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(this.category.getDrag().getX(), this.category.getDrag().getY(), 110.0f, 18.0f, mouseX, mouseY);
        this.category.getDrag().onClick(mouseX, mouseY, button, canDrag);
        this.moduleRects.forEach(moduleRect -> moduleRect.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.category.getDrag().onRelease(state);
        this.moduleRects.forEach(moduleRect -> moduleRect.mouseReleased(mouseX, mouseY, state));
    }
}