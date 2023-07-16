package dev.client.tenacity.utils.objects;

import dev.utils.animations.Animation;
import dev.utils.animations.Direction;
import dev.utils.animations.impl.SmoothStepAnimation;
import org.lwjgl.input.Mouse;

public class Scroll {
    private float maxScroll = Float.MAX_VALUE;
    private float minScroll = 0.0f;
    private float rawScroll;
    private float scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0.0, Direction.BACKWARDS);

    public void onScroll(int ms) {
        this.scroll = (float)((double)this.rawScroll - this.scrollAnimation.getOutput());
        this.rawScroll += (float)Mouse.getDWheel() / 4.0f;
        this.rawScroll = Math.max(Math.min(this.minScroll, this.rawScroll), -this.maxScroll);
        this.scrollAnimation = new SmoothStepAnimation(ms, this.rawScroll - this.scroll, Direction.BACKWARDS);
    }

    public boolean isScrollAnimationDone() {
        return this.scrollAnimation.isDone();
    }

    public float getScroll() {
        this.scroll = (float)((double)this.rawScroll - this.scrollAnimation.getOutput());
        return this.scroll;
    }

    public float getMaxScroll() {
        return this.maxScroll;
    }

    public float getMinScroll() {
        return this.minScroll;
    }

    public float getRawScroll() {
        return this.rawScroll;
    }

    public void setMaxScroll(float maxScroll) {
        this.maxScroll = maxScroll;
    }

    public void setMinScroll(float minScroll) {
        this.minScroll = minScroll;
    }

    public void setRawScroll(float rawScroll) {
        this.rawScroll = rawScroll;
    }
}