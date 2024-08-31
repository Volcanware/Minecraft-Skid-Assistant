package net.minecraft.world.gen.feature;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenDoublePlant extends WorldGenerator {
    private BlockDoublePlant.EnumPlantType field_150549_a;

    public void setPlantType(final BlockDoublePlant.EnumPlantType p_180710_1_) {
        this.field_150549_a = p_180710_1_;
    }

    public boolean generate(final World worldIn, final Random rand, final BlockPos position) {
        boolean flag = false;

        for (int i = 0; i < 64; ++i) {
            final BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.getHasNoSky() || blockpos.getY() < 254) && Blocks.double_plant.canPlaceBlockAt(worldIn, blockpos)) {
                Blocks.double_plant.placeAt(worldIn, blockpos, this.field_150549_a, 2);
                flag = true;
            }
        }

        return flag;
    }
}
