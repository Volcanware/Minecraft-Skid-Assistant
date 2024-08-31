package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFlowerPot extends BlockContainer {
    public static final PropertyInteger LEGACY_DATA = PropertyInteger.create("legacy_data", 0, 15);
    public static final PropertyEnum<BlockFlowerPot.EnumFlowerType> CONTENTS = PropertyEnum.create("contents", BlockFlowerPot.EnumFlowerType.class);

    public BlockFlowerPot() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONTENTS, BlockFlowerPot.EnumFlowerType.EMPTY).withProperty(LEGACY_DATA, Integer.valueOf(0)));
        this.setBlockBoundsForItemRender();
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName() {
        return StatCollector.translateToLocal("item.flowerPot.name");
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        final float f = 0.375F;
        final float f1 = f / 2.0F;
        this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 3;
    }

    public boolean isFullCube() {
        return false;
    }

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityFlowerPot) {
            final Item item = ((TileEntityFlowerPot) tileentity).getFlowerPotItem();

            if (item instanceof ItemBlock) {
                return Block.getBlockFromItem(item).colorMultiplier(worldIn, pos, renderPass);
            }
        }

        return 16777215;
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final ItemStack itemstack = playerIn.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() instanceof ItemBlock) {
            final TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(worldIn, pos);

            if (tileentityflowerpot == null) {
                return false;
            } else if (tileentityflowerpot.getFlowerPotItem() != null) {
                return false;
            } else {
                final Block block = Block.getBlockFromItem(itemstack.getItem());

                if (!this.canNotContain(block, itemstack.getMetadata())) {
                    return false;
                } else {
                    tileentityflowerpot.setFlowerPotData(itemstack.getItem(), itemstack.getMetadata());
                    tileentityflowerpot.markDirty();
                    worldIn.markBlockForUpdate(pos);
                    playerIn.triggerAchievement(StatList.field_181736_T);

                    if (!playerIn.capabilities.isCreativeMode && --itemstack.stackSize <= 0) {
                        playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private boolean canNotContain(final Block blockIn, final int meta) {
        return blockIn == Blocks.yellow_flower || blockIn == Blocks.red_flower || blockIn == Blocks.cactus || blockIn == Blocks.brown_mushroom || blockIn == Blocks.red_mushroom || blockIn == Blocks.sapling || blockIn == Blocks.deadbush || blockIn == Blocks.tallgrass && meta == BlockTallGrass.EnumType.FERN.getMeta();
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        final TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(worldIn, pos);
        return tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null ? tileentityflowerpot.getFlowerPotItem() : Items.flower_pot;
    }

    public int getDamageValue(final World worldIn, final BlockPos pos) {
        final TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(worldIn, pos);
        return tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null ? tileentityflowerpot.getFlowerPotData() : 0;
    }

    /**
     * Returns true only if block is flowerPot
     */
    public boolean isFlowerPot() {
        return true;
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(worldIn, pos);

        if (tileentityflowerpot != null && tileentityflowerpot.getFlowerPotItem() != null) {
            spawnAsEntity(worldIn, pos, new ItemStack(tileentityflowerpot.getFlowerPotItem(), 1, tileentityflowerpot.getFlowerPotData()));
        }

        super.breakBlock(worldIn, pos, state);
    }

    public void onBlockHarvested(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);

        if (player.capabilities.isCreativeMode) {
            final TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(worldIn, pos);

            if (tileentityflowerpot != null) {
                tileentityflowerpot.setFlowerPotData(null, 0);
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.flower_pot;
    }

    private TileEntityFlowerPot getTileEntity(final World worldIn, final BlockPos pos) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot) tileentity : null;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        Block block = null;
        int i = 0;

        switch (meta) {
            case 1:
                block = Blocks.red_flower;
                i = BlockFlower.EnumFlowerType.POPPY.getMeta();
                break;

            case 2:
                block = Blocks.yellow_flower;
                break;

            case 3:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.OAK.getMetadata();
                break;

            case 4:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.SPRUCE.getMetadata();
                break;

            case 5:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.BIRCH.getMetadata();
                break;

            case 6:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.JUNGLE.getMetadata();
                break;

            case 7:
                block = Blocks.red_mushroom;
                break;

            case 8:
                block = Blocks.brown_mushroom;
                break;

            case 9:
                block = Blocks.cactus;
                break;

            case 10:
                block = Blocks.deadbush;
                break;

            case 11:
                block = Blocks.tallgrass;
                i = BlockTallGrass.EnumType.FERN.getMeta();
                break;

            case 12:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.ACACIA.getMetadata();
                break;

            case 13:
                block = Blocks.sapling;
                i = BlockPlanks.EnumType.DARK_OAK.getMetadata();
        }

        return new TileEntityFlowerPot(Item.getItemFromBlock(block), i);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, CONTENTS, LEGACY_DATA);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(LEGACY_DATA).intValue();
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        BlockFlowerPot.EnumFlowerType blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityFlowerPot) {
            final TileEntityFlowerPot tileentityflowerpot = (TileEntityFlowerPot) tileentity;
            final Item item = tileentityflowerpot.getFlowerPotItem();

            if (item instanceof ItemBlock) {
                final int i = tileentityflowerpot.getFlowerPotData();
                final Block block = Block.getBlockFromItem(item);

                if (block == Blocks.sapling) {
                    switch (BlockPlanks.EnumType.byMetadata(i)) {
                        case OAK:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.OAK_SAPLING;
                            break;

                        case SPRUCE:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.SPRUCE_SAPLING;
                            break;

                        case BIRCH:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.BIRCH_SAPLING;
                            break;

                        case JUNGLE:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.JUNGLE_SAPLING;
                            break;

                        case ACACIA:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.ACACIA_SAPLING;
                            break;

                        case DARK_OAK:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DARK_OAK_SAPLING;
                            break;

                        default:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                } else if (block == Blocks.tallgrass) {
                    switch (i) {
                        case 0:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                            break;

                        case 2:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.FERN;
                            break;

                        default:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                } else if (block == Blocks.yellow_flower) {
                    blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DANDELION;
                } else if (block == Blocks.red_flower) {
                    switch (BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, i)) {
                        case POPPY:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.POPPY;
                            break;

                        case BLUE_ORCHID:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.BLUE_ORCHID;
                            break;

                        case ALLIUM:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.ALLIUM;
                            break;

                        case HOUSTONIA:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.HOUSTONIA;
                            break;

                        case RED_TULIP:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.RED_TULIP;
                            break;

                        case ORANGE_TULIP:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.ORANGE_TULIP;
                            break;

                        case WHITE_TULIP:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.WHITE_TULIP;
                            break;

                        case PINK_TULIP:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.PINK_TULIP;
                            break;

                        case OXEYE_DAISY:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
                            break;

                        default:
                            blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                } else if (block == Blocks.red_mushroom) {
                    blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.MUSHROOM_RED;
                } else if (block == Blocks.brown_mushroom) {
                    blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN;
                } else if (block == Blocks.deadbush) {
                    blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                } else if (block == Blocks.cactus) {
                    blockflowerpot$enumflowertype = BlockFlowerPot.EnumFlowerType.CACTUS;
                }
            }
        }

        return state.withProperty(CONTENTS, blockflowerpot$enumflowertype);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public enum EnumFlowerType implements IStringSerializable {
        EMPTY("empty"),
        POPPY("rose"),
        BLUE_ORCHID("blue_orchid"),
        ALLIUM("allium"),
        HOUSTONIA("houstonia"),
        RED_TULIP("red_tulip"),
        ORANGE_TULIP("orange_tulip"),
        WHITE_TULIP("white_tulip"),
        PINK_TULIP("pink_tulip"),
        OXEYE_DAISY("oxeye_daisy"),
        DANDELION("dandelion"),
        OAK_SAPLING("oak_sapling"),
        SPRUCE_SAPLING("spruce_sapling"),
        BIRCH_SAPLING("birch_sapling"),
        JUNGLE_SAPLING("jungle_sapling"),
        ACACIA_SAPLING("acacia_sapling"),
        DARK_OAK_SAPLING("dark_oak_sapling"),
        MUSHROOM_RED("mushroom_red"),
        MUSHROOM_BROWN("mushroom_brown"),
        DEAD_BUSH("dead_bush"),
        FERN("fern"),
        CACTUS("cactus");

        private final String name;

        EnumFlowerType(final String name) {
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
