// 
// Decompiled by Procyon v0.5.36
// 

package vip.radium.gui.csgo.component.impl;

import vip.radium.gui.csgo.component.impl.sub.comboBox.ComboBoxComponent;
import vip.radium.gui.csgo.component.impl.sub.comboBox.ComboBoxTextComponent;
import java.util.Iterator;
import vip.radium.gui.csgo.component.impl.sub.key.KeyBindComponent;
import vip.radium.gui.csgo.component.PredicateComponent;
import vip.radium.gui.font.FontRenderer;
import vip.radium.utils.render.RenderingUtils;
import net.minecraft.client.gui.Gui;
import vip.radium.gui.csgo.SkeetUI;
import vip.radium.utils.render.LockedResolution;
import vip.radium.gui.csgo.component.Component;

public final class GroupBoxComponent extends Component
{
    private final String name;
    
    public GroupBoxComponent(final Component parent, final String name, final float x, final float y, final float width, final float height) {
        super(parent, x, y, width, height);
        this.name = name;
    }
    
    @Override
    public void drawComponent(final LockedResolution resolution, final int mouseX, final int mouseY) {
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        final float height = this.getHeight();
        final float length = SkeetUI.GROUP_BOX_HEADER_RENDERER.getWidth(this.name);
        Gui.drawRect(x, y, x + width, y + height, SkeetUI.getColor(789516));
        Gui.drawRect(x + 0.5f, y + 0.5f, x + width - 0.5f, y + height - 0.5f, SkeetUI.getColor(2631720));
        Gui.drawRect(x + 4.0f, y, x + 4.0f + length + 2.0f, y + 1.0f, SkeetUI.getColor(1513239));
        Gui.drawRect(x + 1.0f, y + 1.0f, x + width - 1.0f, y + height - 1.0f, SkeetUI.getColor(1513239));
        if (SkeetUI.shouldRenderText()) {
            RenderingUtils.drawOutlinedString(SkeetUI.GROUP_BOX_HEADER_RENDERER, this.name, x + 5.0f, y - 0.5f, SkeetUI.getColor(14474460), SkeetUI.getColor(0));
        }
        float childYLeft = 6.0f;
        float childYRight = 6.0f;
        boolean left = true;
        final float right = 49.166668f;
        for (final Component component : this.children) {
            if (component instanceof PredicateComponent) {
                final PredicateComponent predicateComponent = (PredicateComponent)component;
                if (!predicateComponent.isVisible()) {
                    continue;
                }
            }
            else if (component instanceof KeyBindComponent) {
                continue;
            }
            if (component.getWidth() >= 80.333336f) {
                component.setX(3.0f);
                component.setY(childYLeft);
                component.drawComponent(resolution, mouseX, mouseY);
                final float yOffset = component.getHeight() + 4.0f;
                childYLeft += yOffset;
                childYRight += yOffset;
                left = true;
            }
            else {
                component.setX(left ? 3.0f : 49.166668f);
                component.setY(left ? childYLeft : childYRight);
                component.drawComponent(resolution, mouseX, mouseY);
                if (left) {
                    childYLeft += component.getHeight() + 4.0f;
                }
                else {
                    childYRight += component.getHeight() + 4.0f;
                }
                left = (childYRight >= childYLeft);
            }
        }
    }
    
    @Override
    public void onMouseClick(final int mouseX, final int mouseY, final int button) {
        for (final Component child : this.children) {
            if (child instanceof ComboBoxTextComponent) {
                final ComboBoxTextComponent comboBoxTextComponent = (ComboBoxTextComponent)child;
                final ComboBoxComponent comboBox = comboBoxTextComponent.getComboBoxComponent();
                if (!comboBox.isExpanded()) {
                    continue;
                }
                final float x = comboBox.getX();
                final float y = comboBoxTextComponent.getY() + comboBoxTextComponent.getHeight();
                if (mouseX >= x && mouseY > y && mouseX <= x + comboBox.getWidth() && mouseY < y + comboBox.getExpandedHeight()) {
                    comboBoxTextComponent.onMouseClick(mouseX, mouseY, button);
                    return;
                }
                continue;
            }
        }
        super.onMouseClick(mouseX, mouseY, button);
    }
    
    @Override
    public float getHeight() {
        float heightLeft;
        float heightRight;
        final float initHeight = heightRight = (heightLeft = super.getHeight());
        boolean left = true;
        for (final Component component : this.getChildren()) {
            if (component instanceof PredicateComponent) {
                final PredicateComponent predicateComponent = (PredicateComponent)component;
                if (!predicateComponent.isVisible()) {
                    continue;
                }
            }
            if (component.getWidth() >= 80.333336f) {
                final float yOffset = component.getHeight() + 4.0f;
                heightLeft += yOffset;
                heightLeft += yOffset;
                left = true;
            }
            else {
                if (left) {
                    heightLeft += component.getHeight() + 4.0f;
                }
                else {
                    heightRight += component.getHeight() + 4.0f;
                }
                left = (heightRight >= heightLeft);
            }
        }
        final float heightWithComponents = Math.max(heightLeft, heightRight);
        return (heightWithComponents - initHeight > initHeight) ? heightWithComponents : initHeight;
    }
}
