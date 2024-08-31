package me.jellysquid.mods.sodium.common.walden.util;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public enum PlayerUtils
{
	;
	public static GameMode getGameMode(PlayerEntity player)
	{
		PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null) return GameMode.SPECTATOR;
		return playerListEntry.getGameMode();
	}
}
