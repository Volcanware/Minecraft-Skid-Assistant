package dev.client.tenacity.utils.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.module.impl.render.ArraylistMod;
import dev.client.tenacity.utils.misc.HoveringUtil;
import dev.client.tenacity.utils.misc.StringUtils;
import dev.client.tenacity.utils.render.ColorUtil;
import dev.client.tenacity.utils.render.RoundedUtil;
import dev.utils.Utils;
import dev.utils.animations.Animation;
import dev.utils.animations.Direction;
import dev.utils.animations.impl.DecelerateAnimation;
import dev.utils.font.FontUtil;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;

public class Dragging
implements Utils {
    @Expose
    @SerializedName(value="x")
    private float xPos;
    @Expose
    @SerializedName(value="y")
    private float yPos;
    public float initialXVal;
    public float initialYVal;
    private float startX;
    private float startY;
    private boolean dragging;
    private float width;
    private float height;
    @Expose
    @SerializedName(value="name")
    private final String name;
    private final Module module;
    public Animation hoverAnimation = new DecelerateAnimation(250, 1.0, Direction.BACKWARDS);
    private String longestModule;

    public Dragging(Module module, String name, float initialXVal, float initialYVal) {
        this.module = module;
        this.name = name;
        this.xPos = initialXVal;
        this.yPos = initialYVal;
        this.initialXVal = initialXVal;
        this.initialYVal = initialYVal;
    }

    public Module getModule() {
        return this.module;
    }

    public String getName() {
        return this.name;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return this.xPos;
    }

    public void setX(float x) {
        this.xPos = x;
    }

    public float getY() {
        return this.yPos;
    }

    public void setY(float y) {
        this.yPos = y;
    }

    public final void onDraw(int mouseX, int mouseY) {
        boolean hovering = HoveringUtil.isHovering(this.xPos, this.yPos, this.width, this.height, mouseX, mouseY);
        if (this.dragging) {
            this.xPos = (float)mouseX - this.startX;
            this.yPos = (float)mouseY - this.startY;
        }
        this.hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        if (this.hoverAnimation.isDone()) {
            if (!this.hoverAnimation.finished(Direction.FORWARDS)) return;
        }
        RoundedUtil.drawRoundOutline(this.xPos - 4.0f, this.yPos - 4.0f, this.width + 8.0f, this.height + 8.0f, 10.0f, 2.0f, ColorUtil.applyOpacity(Color.WHITE, 0.0f), ColorUtil.applyOpacity(Color.WHITE, (float)this.hoverAnimation.getOutput()));
    }

    public final void onDrawArraylist(ArraylistMod arraylistMod, int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);
        List<Module> modules = StringUtils.getToggledModules(arraylistMod.modules);
        String longest = this.getLongestModule(arraylistMod);
        this.width = (float)(FontUtil.tenacityFont20.getStringWidth(longest) + 5.0);
        this.height = (float)((arraylistMod.height.getValue() + 1.0) * (double)modules.size());
        float textVal = (float)FontUtil.tenacityFont20.getStringWidth(longest);
        float xVal = (float)sr.getScaledWidth() - (textVal + 8.0f + this.xPos);
        if ((float)sr.getScaledWidth() - this.xPos <= (float)sr.getScaledWidth() / 2.0f) {
            xVal += textVal - 2.0f;
        }
        boolean hovering = HoveringUtil.isHovering(xVal, this.yPos, this.width, this.height, mouseX, mouseY);
        if (this.dragging) {
            this.xPos = -((float)mouseX - this.startX);
            this.yPos = (float)mouseY - this.startY;
        }
        this.hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        if (this.hoverAnimation.isDone()) {
            if (!this.hoverAnimation.finished(Direction.FORWARDS)) return;
        }
        RoundedUtil.drawRoundOutline(xVal, this.yPos - 8.0f, this.width + 20.0f, this.height + 16.0f, 10.0f, 2.0f, ColorUtil.applyOpacity(Color.BLACK, (float)(0.0 * this.hoverAnimation.getOutput())), ColorUtil.applyOpacity(Color.WHITE, (float)this.hoverAnimation.getOutput()));
    }

    public final void onClick(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(this.xPos, this.yPos, this.width, this.height, mouseX, mouseY);
        if (button != 0) return;
        if (!canDrag) return;
        this.dragging = true;
        this.startX = (int)((float)mouseX - this.xPos);
        this.startY = (int)((float)mouseY - this.yPos);
    }

    public final void onClickArraylist(ArraylistMod arraylistMod, int mouseX, int mouseY, int button) {
        ScaledResolution sr = new ScaledResolution(mc);
        String longest = this.getLongestModule(arraylistMod);
        float textVal = (float)FontUtil.tenacityFont20.getStringWidth(longest);
        float xVal = (float)sr.getScaledWidth() - (textVal + 8.0f + this.xPos);
        if ((float)sr.getScaledWidth() - this.xPos <= (float)sr.getScaledWidth() / 2.0f) {
            xVal += textVal - 16.0f;
        }
        boolean canDrag = HoveringUtil.isHovering(xVal, this.yPos, this.width, this.height, mouseX, mouseY);
        if (button != 0) return;
        if (!canDrag) return;
        this.dragging = true;
        this.startX = (int)((float)mouseX + this.xPos);
        this.startY = (int)((float)mouseY - this.yPos);
    }

    public final void onRelease(int button) {
        if (button != 0) return;
        this.dragging = false;
    }

    private String getLongestModule(ArraylistMod arraylistMod) {
        return arraylistMod.longest;
    }
}