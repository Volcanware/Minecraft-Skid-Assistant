package net.optifine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.UUID;

public class RandomEntity implements IRandomEntity {
    private Entity entity;

    public int getId() {
        final UUID uuid = this.entity.getUniqueID();
        final long i = uuid.getLeastSignificantBits();
        final int j = (int) (i & 2147483647L);
        return j;
    }

    public BlockPos getSpawnPosition() {
        return this.entity.getDataWatcher().spawnPosition;
    }

    public BiomeGenBase getSpawnBiome() {
        return this.entity.getDataWatcher().spawnBiome;
    }

    public String getName() {
        return this.entity.hasCustomName() ? this.entity.getCustomNameTag() : null;
    }

    public int getHealth() {
        if (!(this.entity instanceof EntityLiving)) {
            return 0;
        } else {
            final EntityLiving entityliving = (EntityLiving) this.entity;
            return (int) entityliving.getHealth();
        }
    }

    public int getMaxHealth() {
        if (!(this.entity instanceof EntityLiving)) {
            return 0;
        } else {
            final EntityLiving entityliving = (EntityLiving) this.entity;
            return (int) entityliving.getMaxHealth();
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(final Entity entity) {
        this.entity = entity;
    }
}
