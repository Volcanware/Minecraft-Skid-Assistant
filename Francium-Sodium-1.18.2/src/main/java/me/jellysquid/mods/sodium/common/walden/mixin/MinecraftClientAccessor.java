package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

    @Accessor
    Session getSession();

}
