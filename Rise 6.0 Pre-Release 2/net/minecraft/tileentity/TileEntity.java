package net.minecraft.tileentity;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.Callable;

public abstract class TileEntity {
    private static final Logger logger = LogManager.getLogger();
    private static final Map<String, Class<? extends TileEntity>> nameToClassMap = Maps.newHashMap();
    private static final Map<Class<? extends TileEntity>, String> classToNameMap = Maps.newHashMap();

    /**
     * the instance of the world the tile entity is in.
     */
    protected World worldObj;
    protected BlockPos pos = BlockPos.ORIGIN;
    protected boolean tileEntityInvalid;
    private int blockMetadata = -1;

    /**
     * the Block type that this TileEntity is contained within
     */
    protected Block blockType;

    /**
     * Adds a new two-way mapping between the class and its string name in both hashmaps.
     */
    private static void addMapping(final Class<? extends TileEntity> cl, final String id) {
        if (nameToClassMap.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id: " + id);
        } else {
            nameToClassMap.put(id, cl);
            classToNameMap.put(cl, id);
        }
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorld() {
        return this.worldObj;
    }

    /**
     * Sets the worldObj for this tileEntity.
     */
    public void setWorldObj(final World worldIn) {
        this.worldObj = worldIn;
    }

    /**
     * Returns true if the worldObj isn't null.
     */
    public boolean hasWorldObj() {
        return this.worldObj != null;
    }

    public void readFromNBT(final NBTTagCompound compound) {
        this.pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
    }

    public void writeToNBT(final NBTTagCompound compound) {
        final String s = classToNameMap.get(this.getClass());

        if (s == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            compound.setString("id", s);
            compound.setInteger("x", this.pos.getX());
            compound.setInteger("y", this.pos.getY());
            compound.setInteger("z", this.pos.getZ());
        }
    }

    /**
     * Creates a new entity and loads its data from the specified NBT.
     */
    public static TileEntity createAndLoadEntity(final NBTTagCompound nbt) {
        TileEntity tileentity = null;

        try {
            final Class<? extends TileEntity> oclass = nameToClassMap.get(nbt.getString("id"));

            if (oclass != null) {
                tileentity = oclass.newInstance();
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

        if (tileentity != null) {
            tileentity.readFromNBT(nbt);
        } else {
            logger.warn("Skipping BlockEntity with id " + nbt.getString("id"));
        }

        return tileentity;
    }

    public int getBlockMetadata() {
        if (this.blockMetadata == -1) {
            final IBlockState iblockstate = this.worldObj.getBlockState(this.pos);
            this.blockMetadata = iblockstate.getBlock().getMetaFromState(iblockstate);
        }

        return this.blockMetadata;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
        if (this.worldObj != null) {
            final IBlockState iblockstate = this.worldObj.getBlockState(this.pos);
            this.blockMetadata = iblockstate.getBlock().getMetaFromState(iblockstate);
            this.worldObj.markChunkDirty(this.pos, this);

            if (this.getBlockType() != Blocks.air) {
                this.worldObj.updateComparatorOutputLevel(this.pos, this.getBlockType());
            }
        }
    }

    /**
     * Returns the square of the distance between this entity and the passed in coordinates.
     */
    public double getDistanceSq(final double x, final double y, final double z) {
        final double d0 = (double) this.pos.getX() + 0.5D - x;
        final double d1 = (double) this.pos.getY() + 0.5D - y;
        final double d2 = (double) this.pos.getZ() + 0.5D - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double getMaxRenderDistanceSquared() {
        return 2048;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    /**
     * Gets the block type at the location of this entity (client-only).
     */
    public Block getBlockType() {
        if (this.blockType == null) {
            this.blockType = this.worldObj.getBlockState(this.pos).getBlock();
        }

        return this.blockType;
    }

    /**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket() {
        return null;
    }

    public boolean isInvalid() {
        return this.tileEntityInvalid;
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate() {
        this.tileEntityInvalid = true;
    }

    /**
     * validates a tile entity
     */
    public void validate() {
        this.tileEntityInvalid = false;
    }

    public boolean receiveClientEvent(final int id, final int type) {
        return false;
    }

    public void updateContainingBlockInfo() {
        this.blockType = null;
        this.blockMetadata = -1;
    }

    public void addInfoToCrashReport(final CrashReportCategory reportCategory) {
        reportCategory.addCrashSectionCallable("Name", new Callable<String>() {
            public String call() throws Exception {
                return TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }
        });

        if (this.worldObj != null) {
            CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockType(), this.getBlockMetadata());
            reportCategory.addCrashSectionCallable("Actual block type", new Callable<String>() {
                public String call() throws Exception {
                    final int i = Block.getIdFromBlock(TileEntity.this.worldObj.getBlockState(TileEntity.this.pos).getBlock());

                    try {
                        return String.format("ID #%d (%s // %s)", Integer.valueOf(i), Block.getBlockById(i).getUnlocalizedName(), Block.getBlockById(i).getClass().getCanonicalName());
                    } catch (final Throwable var3) {
                        return "ID #" + i;
                    }
                }
            });
            reportCategory.addCrashSectionCallable("Actual block data value", new Callable<String>() {
                public String call() throws Exception {
                    final IBlockState iblockstate = TileEntity.this.worldObj.getBlockState(TileEntity.this.pos);
                    final int i = iblockstate.getBlock().getMetaFromState(iblockstate);

                    if (i < 0) {
                        return "Unknown? (Got " + i + ")";
                    } else {
                        final String s = String.format("%4s", Integer.toBinaryString(i)).replace(" ", "0");
                        return String.format("%1$d / 0x%1$X / 0b%2$s", Integer.valueOf(i), s);
                    }
                }
            });
        }
    }

    public void setPos(final BlockPos posIn) {
        this.pos = posIn;
    }

    public boolean func_183000_F() {
        return false;
    }

    static {
        addMapping(TileEntityFurnace.class, "Furnace");
        addMapping(TileEntityChest.class, "Chest");
        addMapping(TileEntityEnderChest.class, "EnderChest");
        addMapping(BlockJukebox.TileEntityJukebox.class, "RecordPlayer");
        addMapping(TileEntityDispenser.class, "Trap");
        addMapping(TileEntityDropper.class, "Dropper");
        addMapping(TileEntitySign.class, "Sign");
        addMapping(TileEntityMobSpawner.class, "MobSpawner");
        addMapping(TileEntityNote.class, "Music");
        addMapping(TileEntityPiston.class, "Piston");
        addMapping(TileEntityBrewingStand.class, "Cauldron");
        addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
        addMapping(TileEntityEndPortal.class, "Airportal");
        addMapping(TileEntityCommandBlock.class, "Control");
        addMapping(TileEntityBeacon.class, "Beacon");
        addMapping(TileEntitySkull.class, "Skull");
        addMapping(TileEntityDaylightDetector.class, "DLDetector");
        addMapping(TileEntityHopper.class, "Hopper");
        addMapping(TileEntityComparator.class, "Comparator");
        addMapping(TileEntityFlowerPot.class, "FlowerPot");
        addMapping(TileEntityBanner.class, "Banner");
    }
}
