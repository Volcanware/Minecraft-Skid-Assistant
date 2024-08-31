// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.item;

import net.minecraft.block.material.Material;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import java.util.Set;

public class ItemPickaxe extends ItemTool
{
    private static final Set<Block> EFFECTIVE_ON;
    
    static {
        EFFECTIVE_ON = Sets.newHashSet((Object[])new Block[] { Blocks.activator_rail, Blocks.coal_ore, Blocks.cobblestone, Blocks.detector_rail, Blocks.diamond_block, Blocks.diamond_ore, Blocks.double_stone_slab, Blocks.golden_rail, Blocks.gold_block, Blocks.gold_ore, Blocks.ice, Blocks.iron_block, Blocks.iron_ore, Blocks.lapis_block, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.mossy_cobblestone, Blocks.netherrack, Blocks.packed_ice, Blocks.rail, Blocks.redstone_ore, Blocks.sandstone, Blocks.red_sandstone, Blocks.stone, Blocks.stone_slab });
    }
    
    protected ItemPickaxe(final ToolMaterial material) {
        super(2.0f, material, ItemPickaxe.EFFECTIVE_ON);
    }
    
    @Override
    public boolean canHarvestBlock(final Block blockIn) {
        return (blockIn == Blocks.obsidian) ? (this.toolMaterial.getHarvestLevel() == 3) : ((blockIn != Blocks.diamond_block && blockIn != Blocks.diamond_ore) ? ((blockIn != Blocks.emerald_ore && blockIn != Blocks.emerald_block) ? ((blockIn != Blocks.gold_block && blockIn != Blocks.gold_ore) ? ((blockIn != Blocks.iron_block && blockIn != Blocks.iron_ore) ? ((blockIn != Blocks.lapis_block && blockIn != Blocks.lapis_ore) ? ((blockIn != Blocks.redstone_ore && blockIn != Blocks.lit_redstone_ore) ? (blockIn.getMaterial() == Material.rock || blockIn.getMaterial() == Material.iron || blockIn.getMaterial() == Material.anvil) : (this.toolMaterial.getHarvestLevel() >= 2)) : (this.toolMaterial.getHarvestLevel() >= 1)) : (this.toolMaterial.getHarvestLevel() >= 1)) : (this.toolMaterial.getHarvestLevel() >= 2)) : (this.toolMaterial.getHarvestLevel() >= 2)) : (this.toolMaterial.getHarvestLevel() >= 2));
    }
    
    @Override
    public float getStrVsBlock(final ItemStack stack, final Block block) {
        return (block.getMaterial() != Material.iron && block.getMaterial() != Material.anvil && block.getMaterial() != Material.rock) ? super.getStrVsBlock(stack, block) : this.efficiencyOnProperMaterial;
    }
}
