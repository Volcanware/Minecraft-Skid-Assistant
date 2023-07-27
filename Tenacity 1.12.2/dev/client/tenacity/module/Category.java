package dev.client.tenacity.module;

import dev.client.tenacity.module.Module;
import dev.client.tenacity.utils.objects.Scroll;
import dev.utils.objects.Drag;

public enum Category {
    COMBAT("Combat", "c"),
    MOVEMENT("Movement", "f"),
    RENDER("Render", "d"),
    PLAYER("Player", "e"),
    EXPLOIT("Exploit", "a"),
    MISC("Misc", "b"),
    SCRIPTS("Scripts", "g");

    public final String name;
    public final String icon;
    public final int posX;
    public final boolean expanded;
    private final Scroll scroll = new Scroll();
    private final Drag drag;
    public int posY = 20;

    private Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
        this.posX = 40 + Module.categoryCount * 120;
        this.drag = new Drag(this.posX, this.posY);
        this.expanded = true;
        ++Module.categoryCount;
    }

    public Scroll getScroll() {
        return this.scroll;
    }

    public Drag getDrag() {
        return this.drag;
    }
}