package me.jellysquid.mods.sodium.common.walden.module.modules.render;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jellysquid.mods.sodium.common.walden.util.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import me.jellysquid.mods.sodium.common.walden.core.Rotation;
import me.jellysquid.mods.sodium.common.walden.core.Rotator;
import me.jellysquid.mods.sodium.common.walden.event.events.AttackEntityListener;
import me.jellysquid.mods.sodium.common.walden.event.events.GameRenderListener;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.stream.Stream;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class PH extends Module implements PlayerTickListener, AttackEntityListener, GameRenderListener
{

	public PH()
	{
		super("Placement Highlight", "Highlights optimal placements for obsidians", false, Category.RENDER);
	}

	private int renderClock = 0;
	private int placeObiClock = -1;

	@Override
	public void onEnable()
	{
		super.onEnable();
		eventManager.add(PlayerTickListener.class, this);
		eventManager.add(AttackEntityListener.class, this);
		eventManager.add(GameRenderListener.class, this);

		renderClock = 0;
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
		eventManager.remove(PlayerTickListener.class, this);
		eventManager.remove(AttackEntityListener.class, this);
		eventManager.remove(GameRenderListener.class, this);
	}

	@Override
	public void onPlayerTick() {

		if (renderClock > 0)
			renderClock--;

		if (placeObiClock == 0)
		{
			placeObiClock = -1;
			if (highlight != null)
			{
				InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN);
				BlockUtils.placeBlock(highlight);
			}
		}
		else
		{
			placeObiClock--;
		}

	}

	private BlockPos highlight;
	private Vec3d targetPredictedPos;

	@Override
	public void onAttackEntity(AttackEntityEvent event)
	{
		if (!(event.getTarget() instanceof PlayerEntity))
			return;
		PlayerEntity target = (PlayerEntity) event.getTarget();
		if (MC.player.isTouchingWater() || MC.player.isInLava())
			return;
		if (!target.isOnGround())
			return;
		int placeCrystalAfter = 4;
		int breakCrystalAfter = 8;
		int placeObiAfter = 2;
		Vec3d targetKB = calcTargetKB(target);
		int floorY = MC.player.getBlockY() - 1;

		Vec3d targetPos = target.getPos();
		Vec3d targetPosAtPlaceCrystal = simulatePos(targetPos, targetKB, placeCrystalAfter);
		Vec3d targetPosAtBreakCrystal = simulatePos(targetPos, targetKB, breakCrystalAfter);
		Vec3d targetPosAtPlaceObi = simulatePos(targetPos, targetKB, placeObiAfter);

		Box targetBoxAtPlaceObi = target.getBoundingBox().offset(targetPosAtPlaceObi.subtract(target.getPos()));
		Box targetBoxAtPlaceCrystal = target.getBoundingBox().offset(targetPosAtPlaceCrystal.subtract(target.getPos()));

		BlockPos blockPos = MC.player.getBlockPos();
		Stream<BlockPos> blocks = BlockUtils.getAllInBoxStream(blockPos.add(-4, 0, -4), blockPos.add(4, 0, 4));

		BlockPos placement = blocks
				.filter(b -> !BlockUtils.hasBlock(b))
				.filter(b -> BlockUtils.hasBlock(b.add(0, -1, 0)))
				.filter(b -> !Box.of(Vec3d.ofCenter(b), 1, 1, 1).intersects(targetBoxAtPlaceObi))
				.filter(b ->
				{
					Vec3d startP = RenderUtils.getCameraPos();
					Vec3d endP = Vec3d.ofBottomCenter(b);
					if (endP.subtract(startP).lengthSquared() > 16)
						return false;
					BlockHitResult result = MC.world.raycast(new RaycastContext(RenderUtils.getCameraPos(), endP, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, MC.player));
					return result.getType() == HitResult.Type.MISS;
				})
				.filter(b -> CrystalUtils.canPlaceCrystalClientAssumeObsidian(b, targetBoxAtPlaceCrystal))
				.max(Comparator.comparingDouble(b -> DamageUtils.crystalDamage(target, targetPosAtBreakCrystal, Vec3d.ofCenter(b, 1), b, false)))
				.orElse(null);
		if (placement == null)
			return;
		Rotator rotator = ConfigManager.INSTANCE.getRotator();
		rotator.stepToward(Vec3d.ofBottomCenter(placement), placeObiAfter, () ->
		{
			InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN);

			BlockPos neighbor = placement.add(0, -1, 0);
			Direction direction = Direction.UP;
			Vec3d center = Vec3d.ofCenter(neighbor).add(Vec3d.of(direction.getVector()).multiply(0.5));
			ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, new BlockHitResult(center, Direction.UP, placement.add(0, -1, 0), false));

			if (result == ActionResult.SUCCESS)
			{
				MC.player.swingHand(Hand.MAIN_HAND);
				rotator.stepToward(new Rotation(0, true, MC.player.getPitch() - 15, false), 5, () ->
				{
					InventoryUtils.selectItemFromHotbar(Items.END_CRYSTAL);
				});
			}
		});
		placeObiClock = placeObiAfter;
		highlight = placement;
		targetPredictedPos = targetPosAtBreakCrystal;
		renderClock = 40;
	}

	private Vec3d simulatePos(Vec3d start, Vec3d velocity, int ticks)
	{
		for (int i = 0; i < ticks; i++)
		{
			double j, k, l;
			j = velocity.getX();
			k = velocity.getY();
			l = velocity.getZ();
			if (Math.abs(j) < 0.003)
				j = 0;
			if (Math.abs(k) < 0.003)
				k = 0;
			if (Math.abs(l) < 0.003)
				l = 0;
			velocity = new Vec3d(j, k, l);
			double g = 0;
			g -= 0.08;
			velocity = velocity.add(0.0D, g * 0.98, 0.0D);
			velocity = velocity.multiply(0.91, 1, 0.91);
			start = start.add(velocity);
		}
		return start;
	}

	private Vec3d calcTargetKB(LivingEntity target)
	{
		float h = MC.player.getAttackCooldownProgress(0.5F);
		int i = EnchantmentHelper.getKnockback(MC.player);
		if (MC.player.isSprinting() && h > 0.9)
			i += 1;
		double strength = (double) ((float) i * 0.5F);
		double x = MathHelper.sin(MC.player.getYaw() * 0.017453292F);
		double z = -MathHelper.cos(MC.player.getYaw() * 0.017453292F);
		Iterable<ItemStack> armors = target.getArmorItems();
		double kbRes = 0;
		for (ItemStack e : armors)
		{
			Item item = e.getItem();
			if (!(item instanceof ArmorItem))
				continue;
			ArmorItem armorItem = (ArmorItem) item;
			if (armorItem.getMaterial() == ArmorMaterials.NETHERITE)
				kbRes += 0.1;
		}
		strength *= 1.0D - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) - kbRes;
		Vec3d result = Vec3d.ZERO;
		if (strength > 0.0)
		{
			Vec3d vec3d = new Vec3d(target.getX() - target.prevX, target.getY() - target.prevY, target.getZ() - target.prevZ);
			Vec3d vec3d2 = (new Vec3d(x, 0.0D, z)).normalize().multiply(strength);
			result = new Vec3d(vec3d.x / 2.0 - vec3d2.x, target.isOnGround() ? Math.min(0.4D, vec3d.y / 2.0D + strength) : vec3d.y, vec3d.z / 2.0D - vec3d2.z);
		}
		return result;
	}

	@Override
	public void onGameRender(MatrixStack matrixStack, float tickDelta)
	{
		if (renderClock == 0 || highlight == null)
			return;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(0.4f, 1.0f, 0.4f, 0.4f);

		matrixStack.push();
		RenderUtils.applyRegionalRenderOffset(matrixStack);
		BlockPos blockPos = RenderUtils.getCameraBlockPos();
		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;

		matrixStack.push();

		matrixStack.translate(highlight.getX() - regionX,
				highlight.getY(), highlight.getZ() - regionZ);

		RenderUtils.drawSolidBox(new Box(BlockPos.ORIGIN), matrixStack);

		matrixStack.pop();

		matrixStack.push();

		matrixStack.translate(targetPredictedPos.getX() - regionX,
				targetPredictedPos.getY(), targetPredictedPos.getZ() - regionZ);
		RenderUtils.drawSolidBox(MC.player.getBoundingBox().offset(MC.player.getPos().multiply(-1)), matrixStack);

		matrixStack.pop();

		matrixStack.pop();

		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
