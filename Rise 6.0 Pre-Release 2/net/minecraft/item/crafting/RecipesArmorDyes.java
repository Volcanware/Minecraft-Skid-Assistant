package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class RecipesArmorDyes implements IRecipe {
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(final InventoryCrafting inv, final World worldIn) {
        ItemStack itemstack = null;
        final List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            final ItemStack itemstack1 = inv.getStackInSlot(i);

            if (itemstack1 != null) {
                if (itemstack1.getItem() instanceof ItemArmor) {
                    final ItemArmor itemarmor = (ItemArmor) itemstack1.getItem();

                    if (itemarmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.dye) {
                        return false;
                    }

                    list.add(itemstack1);
                }
            }
        }

        return itemstack != null && !list.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(final InventoryCrafting inv) {
        ItemStack itemstack = null;
        final int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmor itemarmor = null;

        for (int k = 0; k < inv.getSizeInventory(); ++k) {
            final ItemStack itemstack1 = inv.getStackInSlot(k);

            if (itemstack1 != null) {
                if (itemstack1.getItem() instanceof ItemArmor) {
                    itemarmor = (ItemArmor) itemstack1.getItem();

                    if (itemarmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || itemstack != null) {
                        return null;
                    }

                    itemstack = itemstack1.copy();
                    itemstack.stackSize = 1;

                    if (itemarmor.hasColor(itemstack1)) {
                        final int l = itemarmor.getColor(itemstack);
                        final float f = (float) (l >> 16 & 255) / 255.0F;
                        final float f1 = (float) (l >> 8 & 255) / 255.0F;
                        final float f2 = (float) (l & 255) / 255.0F;
                        i = (int) ((float) i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        aint[0] = (int) ((float) aint[0] + f * 255.0F);
                        aint[1] = (int) ((float) aint[1] + f1 * 255.0F);
                        aint[2] = (int) ((float) aint[2] + f2 * 255.0F);
                        ++j;
                    }
                } else {
                    if (itemstack1.getItem() != Items.dye) {
                        return null;
                    }

                    final float[] afloat = EntitySheep.func_175513_a(EnumDyeColor.byDyeDamage(itemstack1.getMetadata()));
                    final int l1 = (int) (afloat[0] * 255.0F);
                    final int i2 = (int) (afloat[1] * 255.0F);
                    final int j2 = (int) (afloat[2] * 255.0F);
                    i += Math.max(l1, Math.max(i2, j2));
                    aint[0] += l1;
                    aint[1] += i2;
                    aint[2] += j2;
                    ++j;
                }
            }
        }

        if (itemarmor == null) {
            return null;
        } else {
            int i1 = aint[0] / j;
            int j1 = aint[1] / j;
            int k1 = aint[2] / j;
            final float f3 = (float) i / (float) j;
            final float f4 = (float) Math.max(i1, Math.max(j1, k1));
            i1 = (int) ((float) i1 * f3 / f4);
            j1 = (int) ((float) j1 * f3 / f4);
            k1 = (int) ((float) k1 * f3 / f4);
            int lvt_12_3_ = (i1 << 8) + j1;
            lvt_12_3_ = (lvt_12_3_ << 8) + k1;
            itemarmor.setColor(itemstack, lvt_12_3_);
            return itemstack;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize() {
        return 10;
    }

    public ItemStack getRecipeOutput() {
        return null;
    }

    public ItemStack[] getRemainingItems(final InventoryCrafting inv) {
        final ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i) {
            final ItemStack itemstack = inv.getStackInSlot(i);

            if (itemstack != null && itemstack.getItem().hasContainerItem()) {
                aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
            }
        }

        return aitemstack;
    }
}
