package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public abstract class BlockSlab extends Block {
    public static final PropertyEnum<BlockSlab.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockSlab.EnumBlockHalf.class);

    public BlockSlab(final Material materialIn) {
        super(materialIn);

        if (this.isDouble()) {
            this.fullBlock = true;
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

        this.setLightOpacity(255);
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        if (this.isDouble()) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            final IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == this) {
                if (iblockstate.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
                    this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
                }
            }
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        if (this.isDouble()) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     *
     * @param collidingEntity the Entity colliding with this Block
     */
    public void addCollisionBoxesToList(final World worldIn, final BlockPos pos, final IBlockState state, final AxisAlignedBB mask, final List<AxisAlignedBB> list, final Entity collidingEntity) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return this.isDouble();
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        final IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        return this.isDouble() ? iblockstate : (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? iblockstate : iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP));
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random random) {
        return this.isDouble() ? 2 : 1;
    }

    public boolean isFullCube() {
        return this.isDouble();
    }

    public boolean shouldSideBeRendered(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        if (this.isDouble()) {
            return super.shouldSideBeRendered(worldIn, pos, side);
        } else if (side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(worldIn, pos, side)) {
            return false;
        } else {
            final BlockPos blockpos = pos.offset(side.getOpposite());
            final IBlockState iblockstate = worldIn.getBlockState(pos);
            final IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
            final boolean flag = isSlab(iblockstate.getBlock()) && iblockstate.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
            final boolean flag1 = isSlab(iblockstate1.getBlock()) && iblockstate1.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
            return flag1 ? (side == EnumFacing.DOWN || (side == EnumFacing.UP && super.shouldSideBeRendered(worldIn, pos, side) || !isSlab(iblockstate.getBlock()) || !flag)) : (side == EnumFacing.UP || (side == EnumFacing.DOWN && super.shouldSideBeRendered(worldIn, pos, side) || !isSlab(iblockstate.getBlock()) || flag));
        }
    }

    protected static boolean isSlab(final Block blockIn) {
        return blockIn == Blocks.stone_slab || blockIn == Blocks.wooden_slab || blockIn == Blocks.stone_slab2;
    }

    /**
     * Returns the slab block name with the type associated with it
     */
    public abstract String getUnlocalizedName(int meta);

    public int getDamageValue(final World worldIn, final BlockPos pos) {
        return super.getDamageValue(worldIn, pos) & 7;
    }

    public abstract boolean isDouble();

    public abstract IProperty<?> getVariantProperty();

    public abstract Object getVariant(ItemStack stack);

    public enum EnumBlockHalf implements IStringSerializable {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        EnumBlockHalf(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
