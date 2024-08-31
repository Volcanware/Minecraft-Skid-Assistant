package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item {
    public ItemSaddle() {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(final ItemStack stack, final EntityPlayer playerIn, final EntityLivingBase target) {
        if (target instanceof EntityPig) {
            final EntityPig entitypig = (EntityPig) target;

            if (!entitypig.getSaddled() && !entitypig.isChild()) {
                entitypig.setSaddled(true);
                entitypig.worldObj.playSoundAtEntity(entitypig, "mob.horse.leather", 0.5F, 1.0F);
                --stack.stackSize;
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     *
     * @param target   The Entity being hit
     * @param attacker the attacking entity
     */
    public boolean hitEntity(final ItemStack stack, final EntityLivingBase target, final EntityLivingBase attacker) {
        this.itemInteractionForEntity(stack, null, target);
        return true;
    }
}
