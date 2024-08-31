package net.minecraft.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.DynamicLights;

import java.util.ArrayDeque;
import java.util.Arrays;

public class RegionRenderCache extends ChunkCache {
    private static final IBlockState DEFAULT_STATE = Blocks.air.getDefaultState();
    private final BlockPos position;
    private final int[] combinedLights;
    private final IBlockState[] blockStates;
    private static final ArrayDeque<int[]> cacheLights = new ArrayDeque();
    private static final ArrayDeque<IBlockState[]> cacheStates = new ArrayDeque();
    private static final int maxCacheSize = Config.limit(Runtime.getRuntime().availableProcessors(), 1, 32);

    public RegionRenderCache(final World worldIn, final BlockPos posFromIn, final BlockPos posToIn, final int subIn) {
        super(worldIn, posFromIn, posToIn, subIn);
        this.position = posFromIn.subtract(new Vec3i(subIn, subIn, subIn));
        final int i = 8000;
        this.combinedLights = allocateLights(8000);
        Arrays.fill(this.combinedLights, -1);
        this.blockStates = allocateStates(8000);
    }

    public TileEntity getTileEntity(final BlockPos pos) {
        final int i = (pos.getX() >> 4) - this.chunkX;
        final int j = (pos.getZ() >> 4) - this.chunkZ;
        return this.chunkArray[i][j].getTileEntity(pos, Chunk.EnumCreateEntityType.QUEUED);
    }

    public int getCombinedLight(final BlockPos pos, final int lightValue) {
        final int i = this.getPositionIndex(pos);
        int j = this.combinedLights[i];

        if (j == -1) {
            j = super.getCombinedLight(pos, lightValue);

            if (Config.isDynamicLights() && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
                j = DynamicLights.getCombinedLight(pos, j);
            }

            this.combinedLights[i] = j;
        }

        return j;
    }

    public IBlockState getBlockState(final BlockPos pos) {
        final int i = this.getPositionIndex(pos);
        IBlockState iblockstate = this.blockStates[i];

        if (iblockstate == null) {
            iblockstate = this.getBlockStateRaw(pos);
            this.blockStates[i] = iblockstate;
        }

        return iblockstate;
    }

    private IBlockState getBlockStateRaw(final BlockPos pos) {
        return super.getBlockState(pos);
    }

    private int getPositionIndex(final BlockPos p_175630_1_) {
        final int i = p_175630_1_.getX() - this.position.getX();
        final int j = p_175630_1_.getY() - this.position.getY();
        final int k = p_175630_1_.getZ() - this.position.getZ();
        return i * 400 + k * 20 + j;
    }

    public void freeBuffers() {
        freeLights(this.combinedLights);
        freeStates(this.blockStates);
    }

    private static int[] allocateLights(final int p_allocateLights_0_) {
        synchronized (cacheLights) {
            int[] aint = cacheLights.pollLast();

            if (aint == null || aint.length < p_allocateLights_0_) {
                aint = new int[p_allocateLights_0_];
            }

            return aint;
        }
    }

    public static void freeLights(final int[] p_freeLights_0_) {
        synchronized (cacheLights) {
            if (cacheLights.size() < maxCacheSize) {
                cacheLights.add(p_freeLights_0_);
            }
        }
    }

    private static IBlockState[] allocateStates(final int p_allocateStates_0_) {
        synchronized (cacheStates) {
            IBlockState[] aiblockstate = cacheStates.pollLast();

            if (aiblockstate != null && aiblockstate.length >= p_allocateStates_0_) {
                Arrays.fill(aiblockstate, null);
            } else {
                aiblockstate = new IBlockState[p_allocateStates_0_];
            }

            return aiblockstate;
        }
    }

    public static void freeStates(final IBlockState[] p_freeStates_0_) {
        synchronized (cacheStates) {
            if (cacheStates.size() < maxCacheSize) {
                cacheStates.add(p_freeStates_0_);
            }
        }
    }
}
