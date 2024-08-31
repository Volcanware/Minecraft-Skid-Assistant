package me.jellysquid.mods.sodium.common.walden.util;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import me.jellysquid.mods.sodium.common.walden.mixin.MixinConfigClientSide;
import org.jetbrains.annotations.Nullable;

public class AccessorUtils {
    @Nullable
    public static Slot getSlotUnderMouse(HandledScreen<?> gui) {
        return ((MixinConfigClientSide)gui).itemscroller_getHoveredSlot();
    }
}