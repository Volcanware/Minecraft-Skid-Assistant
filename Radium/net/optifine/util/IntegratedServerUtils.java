// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.util;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.entity.Entity;
import java.util.UUID;
import net.minecraft.world.WorldProvider;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.world.WorldServer;

public class IntegratedServerUtils
{
    public static WorldServer getWorldServer() {
        final Minecraft minecraft = Config.getMinecraft();
        final World world = minecraft.theWorld;
        if (world == null) {
            return null;
        }
        if (!minecraft.isIntegratedServerRunning()) {
            return null;
        }
        final IntegratedServer integratedserver = minecraft.getIntegratedServer();
        if (integratedserver == null) {
            return null;
        }
        final WorldProvider worldprovider = world.provider;
        if (worldprovider == null) {
            return null;
        }
        final int i = worldprovider.getDimensionId();
        try {
            final WorldServer worldserver = integratedserver.worldServerForDimension(i);
            return worldserver;
        }
        catch (NullPointerException var6) {
            return null;
        }
    }
    
    public static Entity getEntity(final UUID uuid) {
        final WorldServer worldserver = getWorldServer();
        if (worldserver == null) {
            return null;
        }
        final Entity entity = worldserver.getEntityFromUuid(uuid);
        return entity;
    }
    
    public static TileEntity getTileEntity(final BlockPos pos) {
        final WorldServer worldserver = getWorldServer();
        if (worldserver == null) {
            return null;
        }
        final Chunk chunk = worldserver.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) {
            return null;
        }
        final TileEntity tileentity = chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        return tileentity;
    }
}
