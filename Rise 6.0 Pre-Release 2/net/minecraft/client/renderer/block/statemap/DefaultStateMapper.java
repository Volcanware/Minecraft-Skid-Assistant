package net.minecraft.client.renderer.block.statemap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class DefaultStateMapper extends StateMapperBase {
    protected ModelResourceLocation getModelResourceLocation(final IBlockState state) {
        return new ModelResourceLocation(Block.blockRegistry.getNameForObject(state.getBlock()), this.getPropertyString(state.getProperties()));
    }
}
