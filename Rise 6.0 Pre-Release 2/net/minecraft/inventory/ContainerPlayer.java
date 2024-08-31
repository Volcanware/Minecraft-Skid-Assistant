package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer extends Container {
    /**
     * The crafting matrix inventory.
     */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();

    /**
     * Determines if inventory manipulation should be handled.
     */
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;

    public ContainerPlayer(final InventoryPlayer playerInventory, final boolean localWorld, final EntityPlayer player) {
        this.isLocalWorld = localWorld;
        this.thePlayer = player;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18, 26 + i * 18));
            }
        }

        for (int k = 0; k < 4; ++k) {
            final int k_f = k;
            this.addSlotToContainer(new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - k, 8, 8 + k * 18) {
                public int getSlotStackLimit() {
                    return 1;
                }

                public boolean isItemValid(final ItemStack stack) {
                    return stack != null && (stack.getItem() instanceof ItemArmor ? ((ItemArmor) stack.getItem()).armorType == k_f : ((stack.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || stack.getItem() == Items.skull) && k_f == 0));
                }

                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[k_f];
                }
            });
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(final IInventory inventoryIn) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(final EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        for (int i = 0; i < 4; ++i) {
            final ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

            if (itemstack != null) {
                playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, null);
    }

    public boolean canInteractWith(final EntityPlayer playerIn) {
        return true;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
        ItemStack itemstack = null;
        final Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 1 && index < 5) {
                if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
                    return null;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
                    return null;
                }
            } else if (itemstack.getItem() instanceof ItemArmor && !this.inventorySlots.get(5 + ((ItemArmor) itemstack.getItem()).armorType).getHasStack()) {
                final int i = 5 + ((ItemArmor) itemstack.getItem()).armorType;

                if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
                    return null;
                }
            } else if (index >= 9 && index < 36) {
                if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
                    return null;
                }
            } else if (index >= 36 && index < 45) {
                if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(final ItemStack stack, final Slot p_94530_2_) {
        return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(stack, p_94530_2_);
    }
}
