package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * mixin config client side
 */
@Mixin({ HandledScreen.class })
public interface MixinConfigClientSide
{
    @Accessor("focusedSlot")
    Slot itemscroller_getHoveredSlot();
}