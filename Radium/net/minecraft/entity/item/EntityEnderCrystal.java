// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.entity.item;

import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;

public class EntityEnderCrystal extends Entity
{
    public int innerRotation;
    public int health;
    
    public EntityEnderCrystal(final World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(2.0f, 2.0f);
        this.health = 5;
        this.innerRotation = this.rand.nextInt(100000);
    }
    
    public EntityEnderCrystal(final World worldIn, final double p_i1699_2_, final double p_i1699_4_, final double p_i1699_6_) {
        this(worldIn);
        this.setPosition(p_i1699_2_, p_i1699_4_, p_i1699_6_);
    }
    
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }
    
    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(8, this.health);
    }
    
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.innerRotation;
        this.dataWatcher.updateObject(8, this.health);
        final int i = MathHelper.floor_double(this.posX);
        final int j = MathHelper.floor_double(this.posY);
        final int k = MathHelper.floor_double(this.posZ);
        if (this.worldObj.provider instanceof WorldProviderEnd && this.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock() != Blocks.fire) {
            this.worldObj.setBlockState(new BlockPos(i, j, k), Blocks.fire.getDefaultState());
        }
    }
    
    @Override
    protected void writeEntityToNBT(final NBTTagCompound tagCompound) {
    }
    
    @Override
    protected void readEntityFromNBT(final NBTTagCompound tagCompund) {
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (!this.isDead && !this.worldObj.isRemote) {
            this.health = 0;
            if (this.health <= 0) {
                this.setDead();
                if (!this.worldObj.isRemote) {
                    this.worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 6.0f, true);
                }
            }
        }
        return true;
    }
}
