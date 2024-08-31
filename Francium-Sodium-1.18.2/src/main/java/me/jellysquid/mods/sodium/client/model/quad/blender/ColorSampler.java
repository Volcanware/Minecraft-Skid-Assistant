package me.jellysquid.mods.sodium.client.model.quad.blender;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public interface ColorSampler<T> {
    int getColor(T state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex);
}
