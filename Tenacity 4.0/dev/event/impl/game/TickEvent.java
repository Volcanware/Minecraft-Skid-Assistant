package dev.event.impl.game;

import dev.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class TickEvent extends Event {

    private final int ticks;

    public TickEvent(int ticks) {
        this.ticks = ticks;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getTicks() {
        return ticks;
    }

}
