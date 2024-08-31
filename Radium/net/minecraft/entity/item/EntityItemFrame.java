// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.entity.item;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.Blocks;
import net.minecraft.world.storage.MapData;
import net.minecraft.item.ItemMap;
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EntityHanging;

public class EntityItemFrame extends EntityHanging
{
    private float itemDropChance;
    
    public EntityItemFrame(final World worldIn) {
        super(worldIn);
        this.itemDropChance = 1.0f;
    }
    
    public EntityItemFrame(final World worldIn, final BlockPos p_i45852_2_, final EnumFacing p_i45852_3_) {
        super(worldIn, p_i45852_2_);
        this.itemDropChance = 1.0f;
        this.updateFacingWithBoundingBox(p_i45852_3_);
    }
    
    @Override
    protected void entityInit() {
        this.getDataWatcher().addObjectByDataType(8, 5);
        this.getDataWatcher().addObject(9, (Byte)0);
    }
    
    @Override
    public float getCollisionBorderSize() {
        return 0.0f;
    }
    
    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (!source.isExplosion() && this.getDisplayedItem() != null) {
            if (!this.worldObj.isRemote) {
                this.dropItemOrSelf(source.getEntity(), false);
                this.setDisplayedItem(null);
            }
            return true;
        }
        return super.attackEntityFrom(source, amount);
    }
    
    @Override
    public int getWidthPixels() {
        return 12;
    }
    
    @Override
    public int getHeightPixels() {
        return 12;
    }
    
    @Override
    public boolean isInRangeToRenderDist(final double distance) {
        double d0 = 16.0;
        d0 = d0 * 64.0 * this.renderDistanceWeight;
        return distance < d0 * d0;
    }
    
    @Override
    public void onBroken(final Entity brokenEntity) {
        this.dropItemOrSelf(brokenEntity, true);
    }
    
    public void dropItemOrSelf(final Entity p_146065_1_, final boolean p_146065_2_) {
        if (this.worldObj.getGameRules().getGameRuleBooleanValue("doEntityDrops")) {
            ItemStack itemstack = this.getDisplayedItem();
            if (p_146065_1_ instanceof EntityPlayer) {
                final EntityPlayer entityplayer = (EntityPlayer)p_146065_1_;
                if (entityplayer.capabilities.isCreativeMode) {
                    this.removeFrameFromMap(itemstack);
                    return;
                }
            }
            if (p_146065_2_) {
                this.entityDropItem(new ItemStack(Items.item_frame), 0.0f);
            }
            if (itemstack != null && this.rand.nextFloat() < this.itemDropChance) {
                itemstack = itemstack.copy();
                this.removeFrameFromMap(itemstack);
                this.entityDropItem(itemstack, 0.0f);
            }
        }
    }
    
    private void removeFrameFromMap(final ItemStack p_110131_1_) {
        if (p_110131_1_ != null) {
            if (p_110131_1_.getItem() == Items.filled_map) {
                final MapData mapdata = ((ItemMap)p_110131_1_.getItem()).getMapData(p_110131_1_, this.worldObj);
                mapdata.playersVisibleOnMap.remove("frame-" + this.getEntityId());
            }
            p_110131_1_.setItemFrame(null);
        }
    }
    
    public ItemStack getDisplayedItem() {
        return this.getDataWatcher().getWatchableObjectItemStack(8);
    }
    
    public void setDisplayedItem(final ItemStack p_82334_1_) {
        this.setDisplayedItemWithUpdate(p_82334_1_, true);
    }
    
    private void setDisplayedItemWithUpdate(ItemStack p_174864_1_, final boolean p_174864_2_) {
        if (p_174864_1_ != null) {
            p_174864_1_ = p_174864_1_.copy();
            p_174864_1_.stackSize = 1;
            p_174864_1_.setItemFrame(this);
        }
        this.getDataWatcher().updateObject(8, p_174864_1_);
        this.getDataWatcher().setObjectWatched(8);
        if (p_174864_2_ && this.hangingPosition != null) {
            this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
        }
    }
    
    public int getRotation() {
        return this.getDataWatcher().getWatchableObjectByte(9);
    }
    
    public void setItemRotation(final int p_82336_1_) {
        this.func_174865_a(p_82336_1_, true);
    }
    
    private void func_174865_a(final int p_174865_1_, final boolean p_174865_2_) {
        this.getDataWatcher().updateObject(9, (byte)(p_174865_1_ % 8));
        if (p_174865_2_ && this.hangingPosition != null) {
            this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
        }
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound tagCompound) {
        if (this.getDisplayedItem() != null) {
            tagCompound.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
            tagCompound.setByte("ItemRotation", (byte)this.getRotation());
            tagCompound.setFloat("ItemDropChance", this.itemDropChance);
        }
        super.writeEntityToNBT(tagCompound);
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound tagCompund) {
        final NBTTagCompound nbttagcompound = tagCompund.getCompoundTag("Item");
        if (nbttagcompound != null && !nbttagcompound.hasNoTags()) {
            this.setDisplayedItemWithUpdate(ItemStack.loadItemStackFromNBT(nbttagcompound), false);
            this.func_174865_a(tagCompund.getByte("ItemRotation"), false);
            if (tagCompund.hasKey("ItemDropChance", 99)) {
                this.itemDropChance = tagCompund.getFloat("ItemDropChance");
            }
            if (tagCompund.hasKey("Direction")) {
                this.func_174865_a(this.getRotation() * 2, false);
            }
        }
        super.readEntityFromNBT(tagCompund);
    }
    
    @Override
    public boolean interactFirst(final EntityPlayer playerIn) {
        if (this.getDisplayedItem() == null) {
            final ItemStack itemstack = playerIn.getHeldItem();
            if (itemstack != null && !this.worldObj.isRemote) {
                this.setDisplayedItem(itemstack);
                if (!playerIn.capabilities.isCreativeMode) {
                    final ItemStack itemStack = itemstack;
                    if (--itemStack.stackSize <= 0) {
                        playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, null);
                    }
                }
            }
        }
        else if (!this.worldObj.isRemote) {
            this.setItemRotation(this.getRotation() + 1);
        }
        return true;
    }
    
    public int func_174866_q() {
        return (this.getDisplayedItem() == null) ? 0 : (this.getRotation() % 8 + 1);
    }
}
