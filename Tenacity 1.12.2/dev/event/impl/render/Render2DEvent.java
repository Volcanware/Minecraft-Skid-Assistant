package dev.event.impl.render;

import dev.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent
extends Event {
    public ScaledResolution sr;
    private float width;
    private float height;

    public Render2DEvent(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Render2DEvent(ScaledResolution sr) {
        this.sr = sr;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}