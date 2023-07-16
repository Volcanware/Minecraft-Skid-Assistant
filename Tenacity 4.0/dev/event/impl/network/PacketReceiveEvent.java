package dev.event.impl.network;

import dev.event.Event;
import net.minecraft.network.Packet;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class PacketReceiveEvent extends Event {

    private Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet) {
        this.packet = packet;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public Packet<?> getPacket() {
        return packet;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

}
