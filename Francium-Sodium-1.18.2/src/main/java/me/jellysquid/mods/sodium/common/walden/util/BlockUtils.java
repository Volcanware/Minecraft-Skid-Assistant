package me.jellysquid.mods.sodium.common.walden.util;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.stream.Stream;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public enum BlockUtils
{
	;

	private static void addToArrayIfHasBlock(ArrayList<BlockPos> array, BlockPos pos)
	{
		if (hasBlock(pos) && !isBlockReplaceable(pos))
			array.add(pos);
	}

	public static ArrayList<BlockPos> getClickableNeighbors(BlockPos pos)
	{
		ArrayList<BlockPos> blocks = new ArrayList<>();
		addToArrayIfHasBlock(blocks, pos.add( 1,  0,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0,  1,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0,  0,  1));
		addToArrayIfHasBlock(blocks, pos.add(-1,  0,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0, -1,  0));
		addToArrayIfHasBlock(blocks, pos.add( 0,  0, -1));
		return blocks;
	}

	public static boolean placeBlock(BlockPos pos)
	{
		return placeBlock(pos, getDefaultBlockState());
	}

	public static boolean placeBlock(BlockPos pos, BlockState state)
	{
		// if block is replaceable
//		if (hasBlock(pos) && BlockUtils.isBlockReplaceable(pos))
//		{
//			BlockState blockToReplace = BlockUtils.getBlockState(pos);
//			Vec3d center = blockToReplace.getOutlineShape(MC.world, pos)
//					.getBoundingBox().getCenter();
//
//
//
//			// fake rotation
//			CWHACK.getRotationFaker().setServerLookPos(center);
//
//			// get raycast result
//			BlockHitResult hitResult = BlockUtils.serverRaycastBlock(pos);
//			ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hitResult);
//
//			// swing hand
//			boolean succeed = result == ActionResult.SUCCESS;
//			if (succeed)
//				MC.player.swingHand(Hand.MAIN_HAND);
//
//			// return
//			return succeed;
//		}

		// if there has already been a block
		if (hasBlock(pos))
			return false;

		if (!BlockUtils.canPlace(state, pos))
			return false;

		// if there is no clickable neighbors
		ArrayList<BlockPos> neighbors = BlockUtils.getClickableNeighbors(pos);
		if (neighbors.isEmpty())
			return false;

		// find the correct neighbor to click on
		BlockPos neighborToClick = null;
		Direction directionToClick = null;
		Vec3d faceCenterToClick = null;
		for (BlockPos neighbor : neighbors)
		{
			BlockState block = BlockUtils.getBlockState(neighbor);
			Direction correctFace = null;

			// iterate through 6 faces to find the correct face
			for (Direction face : Direction.values())
			{
				if (pos.equals(neighbor.add(face.getVector())))
				{
					correctFace = face;
					break;
				}
			}

			Vec3d faceCenter = Vec3d.ofCenter(neighbor).add(Vec3d.of(correctFace.getVector()).multiply(0.5));

			BlockHitResult hit = MC.world.raycastBlock(RotationUtils.getEyesPos(), faceCenter, neighbor, BlockUtils.getBlockState(neighbor).getOutlineShape(MC.world, neighbor), BlockUtils.getBlockState(neighbor));
			if (hit == null)
			{
				neighborToClick = neighbor;
				directionToClick = correctFace;
				faceCenterToClick = faceCenter;
				break;
			}
		}

		// if no viable neighbor found
		if (neighborToClick == null)
			return false;

		//CWHACK.getRotationFaker().setServerLookPos(faceCenterToClick);

		ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, new BlockHitResult(faceCenterToClick, directionToClick, neighborToClick, false));

		if (result == ActionResult.SUCCESS)
		{
			MC.player.swingHand(Hand.MAIN_HAND);
			return true;
		}

		return false;
	}

	public static boolean canPlace(BlockState state, BlockPos pos)
	{
		return MC.world.canPlace(state, pos, null);
	}

	public static boolean hasBlock(BlockPos pos)
	{
		return !MC.world.getBlockState(pos).isAir();
	}

	public static boolean isBlock(Block block, BlockPos pos)
	{
		return getBlockState(pos).getBlock() == block;
	}

	public static Block getBlock(BlockPos pos)
	{
		return MC.world.getBlockState(pos).getBlock();
	}

	public static BlockState getBlockState(BlockPos pos)
	{
		return MC.world.getBlockState(pos);
	}

	public static BlockState getDefaultBlockState()
	{
		return Blocks.STONE.getDefaultState();
	}

	public static boolean isBlockReplaceable(BlockPos pos)
	{
		return getBlockState(pos).getMaterial().isReplaceable();
	}

	public static Stream<BlockPos> getAllInBoxStream(BlockPos from, BlockPos to)
	{
		BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()),
				Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
		BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()),
				Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

		Stream<BlockPos> stream = Stream.iterate(min, pos -> {

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			x++;

			if(x > max.getX())
			{
				x = min.getX();
				y++;
			}

			if(y > max.getY())
			{
				y = min.getY();
				z++;
			}

			if(z > max.getZ())
				throw new IllegalStateException("Stream limit didn't work.");

			return new BlockPos(x, y, z);
		});

		int limit = (max.getX() - min.getX() + 1)
				* (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);

		return stream.limit(limit);
	}

}
