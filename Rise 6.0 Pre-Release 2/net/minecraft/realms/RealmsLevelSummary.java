package net.minecraft.realms;

import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsLevelSummary implements Comparable<RealmsLevelSummary> {
    private final SaveFormatComparator levelSummary;

    public RealmsLevelSummary(final SaveFormatComparator p_i1109_1_) {
        this.levelSummary = p_i1109_1_;
    }

    public int getGameMode() {
        return this.levelSummary.getEnumGameType().getID();
    }

    public String getLevelId() {
        return this.levelSummary.getFileName();
    }

    public boolean hasCheats() {
        return this.levelSummary.getCheatsEnabled();
    }

    public boolean isHardcore() {
        return this.levelSummary.isHardcoreModeEnabled();
    }

    public boolean isRequiresConversion() {
        return this.levelSummary.requiresConversion();
    }

    public String getLevelName() {
        return this.levelSummary.getDisplayName();
    }

    public long getLastPlayed() {
        return this.levelSummary.getLastTimePlayed();
    }

    public int compareTo(final SaveFormatComparator p_compareTo_1_) {
        return this.levelSummary.compareTo(p_compareTo_1_);
    }

    public long getSizeOnDisk() {
        return this.levelSummary.getSizeOnDisk();
    }

    public int compareTo(final RealmsLevelSummary p_compareTo_1_) {
        return this.levelSummary.getLastTimePlayed() < p_compareTo_1_.getLastPlayed() ? 1 : (this.levelSummary.getLastTimePlayed() > p_compareTo_1_.getLastPlayed() ? -1 : this.levelSummary.getFileName().compareTo(p_compareTo_1_.getLevelId()));
    }
}
