package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIOcelotSit extends EntityAIMoveToBlock {
    private final EntityOcelot field_151493_a;

    public EntityAIOcelotSit(final EntityOcelot p_i45315_1_, final double p_i45315_2_) {
        super(p_i45315_1_, p_i45315_2_, 8);
        this.field_151493_a = p_i45315_1_;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return this.field_151493_a.isTamed() && !this.field_151493_a.isSitting() && super.shouldExecute();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        super.startExecuting();
        this.field_151493_a.getAISit().setSitting(false);
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        super.resetTask();
        this.field_151493_a.setSitting(false);
    }

    /**
     * Updates the task
     */
    public void updateTask() {
        super.updateTask();
        this.field_151493_a.getAISit().setSitting(false);

        if (!this.getIsAboveDestination()) {
            this.field_151493_a.setSitting(false);
        } else if (!this.field_151493_a.isSitting()) {
            this.field_151493_a.setSitting(true);
        }
    }

    /**
     * Return true to set given position as destination
     */
    protected boolean shouldMoveTo(final World worldIn, final BlockPos pos) {
        if (!worldIn.isAirBlock(pos.up())) {
            return false;
        } else {
            final IBlockState iblockstate = worldIn.getBlockState(pos);
            final Block block = iblockstate.getBlock();

            if (block == Blocks.chest) {
                final TileEntity tileentity = worldIn.getTileEntity(pos);

                return tileentity instanceof TileEntityChest && ((TileEntityChest) tileentity).numPlayersUsing < 1;
            } else {
                if (block == Blocks.lit_furnace) {
                    return true;
                }

                return block == Blocks.bed && iblockstate.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD;
            }
        }
    }
}
