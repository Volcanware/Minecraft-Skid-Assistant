package net.minecraft.client.audio;

public class SoundEventAccessor implements ISoundEventAccessor<SoundPoolEntry> {
    private final SoundPoolEntry entry;
    private final int weight;

    SoundEventAccessor(final SoundPoolEntry entry, final int weight) {
        this.entry = entry;
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

    public SoundPoolEntry cloneEntry() {
        return new SoundPoolEntry(this.entry);
    }
}
