package dev.event.impl.player;

import dev.event.Event;

public final class ChatReceivedEvent extends Event {
    private String text;

    public ChatReceivedEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}