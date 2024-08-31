package me.jellysquid.mods.sodium.common.walden.util;

import net.minecraft.block.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import me.jellysquid.mods.sodium.common.walden.ConfigManager;

import java.util.ArrayList;
import java.util.stream.Stream;

import static net.minecraft.util.shape.VoxelShapes.fullCube;

public enum AnchorBlockUtils {;
    public static boolean isAnchorCharged(BlockPos anchor) {
        if (!isBlock(Blocks.RESPAWN_ANCHOR, anchor)) {
            return false;
        } else {
            try {
                return (Integer) BlockUtils.getBlockState(anchor).get(RespawnAnchorBlock.CHARGES) != 0;
            } catch (IllegalArgumentException var2) {
                return false;
            }
        }
    }

    public static boolean isAnchorUncharged(BlockPos anchor) {
        if (!isBlock(Blocks.RESPAWN_ANCHOR, anchor)) {
            return false;
        } else {
            try {
                return (Integer) BlockUtils.getBlockState(anchor).get(RespawnAnchorBlock.CHARGES) == 0;
            } catch (IllegalArgumentException var2) {
                return false;
            }
        }
    }

    public static boolean canPlace(BlockState state, BlockPos pos) {
        return ConfigManager.MC.world.canPlace(state, pos, (ShapeContext)null);
    }

    public static boolean hasBlock(BlockPos pos) {
        return !ConfigManager.MC.world.getBlockState(pos).isAir();
    }

    public static boolean isBlock(Block block, BlockPos pos) {
        return getBlockState(pos).getBlock() == block;
    }

    public static Block getBlock(BlockPos pos) {
        return ConfigManager.MC.world.getBlockState(pos).getBlock();
    }

    public static BlockState getBlockState(BlockPos pos) {
        return ConfigManager.MC.world.getBlockState(pos);
    }

    public static BlockState getDefaultBlockState() {
        return Blocks.STONE.getDefaultState();
    }

    public static boolean isBlockReplaceable(BlockPos pos) {
        return getBlockState(pos).getMaterial().isReplaceable();
    }

    private static void addToArrayIfHasBlock(ArrayList<BlockPos> array, BlockPos pos) {
        if (hasBlock(pos) && !isBlockReplaceable(pos)) {
            array.add(pos);
        }

    }

    public static ArrayList<BlockPos> getClickableNeighbors(BlockPos pos) {
        ArrayList<BlockPos> blocks = new ArrayList();
        addToArrayIfHasBlock(blocks, pos.add(1, 0, 0));
        addToArrayIfHasBlock(blocks, pos.add(0, 1, 0));
        addToArrayIfHasBlock(blocks, pos.add(0, 0, 1));
        addToArrayIfHasBlock(blocks, pos.add(-1, 0, 0));
        addToArrayIfHasBlock(blocks, pos.add(0, -1, 0));
        addToArrayIfHasBlock(blocks, pos.add(0, 0, -1));
        return blocks;
    }

    public static BlockHitResult clientRaycastBlock(BlockPos pos) {
        return ConfigManager.MC.world.raycastBlock(RotationUtils.getEyesPos(), RotationUtils.getClientLookVec().multiply(6.0D).add(RotationUtils.getEyesPos()), pos, getBlockState(pos).getOutlineShape(ConfigManager.MC.world, pos), getBlockState(pos));
    }

    public static BlockHitResult serverRaycastBlock(BlockPos pos) {
        return ConfigManager.MC.world.raycastBlock(RotationUtils.getEyesPos(), RotationUtils.getClientLookVec().multiply(6.0D).add(RotationUtils.getEyesPos()), pos, getBlockState(pos).getOutlineShape(ConfigManager.MC.world, pos), getBlockState(pos));
    }

    public static Stream<BlockPos> getAllInBoxStream(BlockPos from, BlockPos to) {
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        Stream<BlockPos> stream = Stream.iterate(min, (pos) -> {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            ++x;
            if (x > max.getX()) {
                x = min.getX();
                ++y;
            }

            if (y > max.getY()) {
                y = min.getY();
                ++z;
            }

            if (z > max.getZ()) {
                throw new IllegalStateException("Stream limit didn't work.");
            } else {
                return new BlockPos(x, y, z);
            }
        });
        int limit = (max.getX() - min.getX() + 1) * (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);
        return stream.limit((long)limit);
    }

    public static boolean isBlockReachable(BlockPos pos, double reach) {
        BlockState state = BlockUtils.getBlockState(pos);
        VoxelShape shape = state.getOutlineShape(ConfigManager.MC.world, pos);
        if (shape.isEmpty()) {
            shape = fullCube();
        }

        Vec3d eyesPos = RotationUtils.getEyesPos();
        Vec3d relCenter = shape.getBoundingBox().getCenter();
        Vec3d center = Vec3d.of((Vec3i)pos).add(relCenter);
        boolean reachable = false;
        Direction[] var9 = Direction.values();
        int var10 = var9.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            Direction direction = var9[var11];
            Vec3i dirVec = direction.getVector();
            Vec3d relHitVec = new Vec3d(relCenter.x * (double)dirVec.getX(), relCenter.y * (double)dirVec.getY(), relCenter.z * (double)dirVec.getZ());
            Vec3d hitVec = center.add(relHitVec);
            if (eyesPos.squaredDistanceTo(hitVec) <= reach * reach) {
                reachable = true;
            }
        }

        return reachable;
    }

    public static boolean rightClickBlock(BlockPos pos) {
        Direction side = null;
        Direction[] sides = Direction.values();
        BlockState state = BlockUtils.getBlockState(pos);
        VoxelShape shape = state.getOutlineShape(ConfigManager.MC.world, pos);
        if (shape.isEmpty()) {
            return false;
        } else {
            Vec3d eyesPos = RotationUtils.getEyesPos();
            Vec3d relCenter = shape.getBoundingBox().getCenter();
            Vec3d center = Vec3d.of((Vec3i)pos).add(relCenter);
            Vec3d[] hitVecs = new Vec3d[sides.length];


            for(int i = 0; i < sides.length; ++i) {
                Vec3i dirVec = sides[i].getVector();
                Vec3d relHitVec = new Vec3d(relCenter.x * (double)dirVec.getX(), relCenter.y * (double)dirVec.getY(), relCenter.z * (double)dirVec.getZ());
                hitVecs[i] = center.add(relHitVec);
            }

            double distanceSqToCenter = eyesPos.squaredDistanceTo(center);

            for(int i = 0; i < sides.length; ++i) {
                if (!(eyesPos.squaredDistanceTo(hitVecs[i]) >= distanceSqToCenter)) {
                    side = sides[i];
                    break;
                }
            }

            if (side == null) {
                side = sides[0];
            }

            ActionResult result1 = null;
            ActionResult result2 = ConfigManager.MC.interactionManager.interactBlock(ConfigManager.MC.player,ConfigManager.MC.world, Hand.MAIN_HAND, new BlockHitResult(hitVecs[side.ordinal()], side, pos, false));
            boolean bl = result1 == ActionResult.SUCCESS || result2 == ActionResult.SUCCESS;
            if (bl) {
                ConfigManager.MC.player.swingHand(Hand.MAIN_HAND);
            }

            return bl;
        }
    }

    public static boolean isContainer(BlockPos pos) {
        Block block = getBlock(pos);
        return block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL || block instanceof ShulkerBoxBlock;
    }

    // $FF: synthetic method
    private static AnchorBlockUtils[] $values() {
        return new AnchorBlockUtils[0];
    }
}
