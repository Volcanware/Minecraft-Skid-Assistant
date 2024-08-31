package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.DecimalSetting;
import me.jellysquid.mods.sodium.common.walden.util.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ADH extends Module implements PlayerTickListener
{
	private final BooleanSetting dhandafterpop;
	private final BooleanSetting dhandAtHealth;
	private final DecimalSetting dHandHealth;
	private final BooleanSetting checkPlayersAround;
	private final DecimalSetting distance;
	private final BooleanSetting predictCrystals;
	private final BooleanSetting checkEnemiesAim;
	private final BooleanSetting checkHoldingItems;
	private final DecimalSetting activatesAbove;
	private boolean BelowHearts;
	private boolean noOffhandTotem;

	public ADH() {
		super("Auto Double Hand", "Automatically double hand when you appear to be in a predicament", false, Category.COMBAT);
		this.dhandafterpop = BooleanSetting.Builder.newInstance().setName("dHand after Pop").setDescription("Automatically dHands afer a pop").setModule(this).setValue(false).setAvailability(() -> true).build();
		this.dhandAtHealth = BooleanSetting.Builder.newInstance().setName("dHand at Health").setDescription("when enabled, it will dhand at a sertain health").setModule(this).setValue(false).setAvailability(() -> true).build();
		final DecimalSetting.Builder setStep = DecimalSetting.Builder.newInstance().setName("dHand Health").setDescription("What Health to automatically doublehand on").setModule(this).setValue(2.0).setMin(1.0).setMax(20.0).setStep(1.0);
		final BooleanSetting dhandAtHealth = this.dhandAtHealth;
		Objects.requireNonNull(dhandAtHealth);
		this.dHandHealth = setStep.setAvailability(dhandAtHealth::get).build();
		this.checkPlayersAround = BooleanSetting.Builder.newInstance().setName("Check Around Players").setDescription("if on, ADH will only activate when players are around").setModule(this).setValue(false).setAvailability(() -> true).build();
		final DecimalSetting.Builder setStep2 = DecimalSetting.Builder.newInstance().setName("Distance").setDescription("the distance for your enemy to activate").setModule(this).setValue(5.0).setMin(1.0).setMax(10.0).setStep(0.1);
		final BooleanSetting checkPlayersAround = this.checkPlayersAround;
		Objects.requireNonNull(checkPlayersAround);
		this.distance = setStep2.setAvailability(checkPlayersAround::get).build();
		this.predictCrystals = BooleanSetting.Builder.newInstance().setName("Predict Crystals").setDescription("whether or not to predict crystal placements").setModule(this).setValue(false).setAvailability(() -> true).build();
		final BooleanSetting.Builder setValue = BooleanSetting.Builder.newInstance().setName("Check Aim").setDescription("when enabled, crystal prediction will only activate when someone is pointing at an obsidian").setModule(this).setValue(false);
		final BooleanSetting predictCrystals = this.predictCrystals;
		Objects.requireNonNull(predictCrystals);
		this.checkEnemiesAim = setValue.setAvailability(predictCrystals::get).build();
		this.checkHoldingItems = BooleanSetting.Builder.newInstance().setName("Check Items").setDescription("when enabled, crystal prediction will only activate when someone is pointing at an obsidian with crystals out").setModule(this).setValue(false).setAvailability(() -> this.predictCrystals.get() && this.checkEnemiesAim.get()).build();
		this.activatesAbove = DecimalSetting.Builder.newInstance().setName("Activation Hight").setDescription("ADH will only activate when you are above this height, set to 0 to disable").setModule(this).setValue(0.2).setMin(0.0).setMax(4.0).setStep(0.1).setAvailability(() -> true).build();
		this.BelowHearts = false;
		this.noOffhandTotem = false;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		ADH.eventManager.add(PlayerTickListener.class, this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		ADH.eventManager.remove(PlayerTickListener.class, this);
	}

	private List<EndCrystalEntity> getNearByCrystals() {
		final Vec3d pos = ConfigManager.MC.player.getPos();
		return (List<EndCrystalEntity>) ConfigManager.MC.world.getEntitiesByClass((Class)EndCrystalEntity.class, new Box(pos.add(-6.0, -6.0, -6.0), pos.add(6.0, 6.0, 6.0)), a -> true);
	}

	@Override
	public void onPlayerTick() {
		final double distanceSq = this.distance.get() * this.distance.get();
		final PlayerInventory inv = ADH.mc.player.getInventory();
		if (((ItemStack)inv.offHand.get(0)).getItem() != Items.TOTEM_OF_UNDYING && this.dhandafterpop.get() && !this.noOffhandTotem) {
			this.noOffhandTotem = true;
			InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
		}
		if (((ItemStack)inv.offHand.get(0)).getItem() == Items.TOTEM_OF_UNDYING) {
			this.noOffhandTotem = false;
		}
		if (ConfigManager.MC.player.getHealth() <= this.dHandHealth.get() && this.dhandAtHealth.get() && !this.BelowHearts) {
			this.BelowHearts = true;
			InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
		}
		if (ConfigManager.MC.player.getHealth() > this.dHandHealth.get()) {
			this.BelowHearts = false;
		}
		if (ConfigManager.MC.player.getHealth() > 19.0f) {
			return;
		}
		if (this.checkPlayersAround.get() && ConfigManager.MC.world.getPlayers().parallelStream().filter(e -> e != ConfigManager.MC.player).noneMatch(player -> ConfigManager.MC.player.squaredDistanceTo(player) <= distanceSq)) {
			return;
		}
		final double activatesAboveV = this.activatesAbove.get();
		for (int f = (int)Math.floor(activatesAboveV), i = 1; i <= f; ++i) {
			if (BlockUtils.hasBlock(ConfigManager.MC.player.getBlockPos().add(0, -i, 0))) {
				return;
			}
		}
		if (BlockUtils.hasBlock(new BlockPos(ConfigManager.MC.player.getPos().add(0.0, -activatesAboveV, 0.0)))) {
			return;
		}
		final List<EndCrystalEntity> crystals = this.getNearByCrystals();
		final ArrayList<Vec3d> crystalsPos = new ArrayList<Vec3d>();
		crystals.forEach(e -> crystalsPos.add(e.getPos()));
		if (this.predictCrystals.get()) {
			Stream<BlockPos> stream = BlockUtils.getAllInBoxStream(ConfigManager.MC.player.getBlockPos().add(-6, -8, -6), ConfigManager.MC.player.getBlockPos().add(6, 2, 6)).filter(e -> BlockUtils.isBlock(Blocks.OBSIDIAN, e) || BlockUtils.isBlock(Blocks.BEDROCK, e)).filter(CrystalUtils::canPlaceCrystalClient);
			if (this.checkEnemiesAim.get()) {
				if (this.checkHoldingItems.get()) {
					stream = stream.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
				}
				else {
					stream = stream.filter(this::arePeopleAimingAtBlock);
				}
			}
			stream.forEachOrdered(e -> crystalsPos.add(Vec3d.ofBottomCenter(e).add(0.0, 1.0, 0.0)));
		}
		for (final Vec3d pos : crystalsPos) {
			final double damage = DamageUtils.crystalDamage((PlayerEntity) ConfigManager.MC.player, pos, true, null, false);
			if (damage >= ConfigManager.MC.player.getHealth() + ConfigManager.MC.player.getAbsorptionAmount()) {
				InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
				break;
			}
		}
	}

	private boolean arePeopleAimingAtBlock(final BlockPos block) {
		final Vec3d[] eyesPos = new Vec3d[1];
		final BlockHitResult[] hitResult = new BlockHitResult[1];
		return ConfigManager.MC.world.getPlayers().parallelStream().filter(e -> e != ConfigManager.MC.player).anyMatch(e -> {
			eyesPos[0] = RotationUtils.getEyesPos(e);
			hitResult[0] = ConfigManager.MC.world.raycast(new RaycastContext(eyesPos[0], eyesPos[0].add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity) e));
			return hitResult[0] != null && hitResult[0].getBlockPos().equals((Object)block);
		});
	}

	private boolean arePeopleAimingAtBlockAndHoldingCrystals(final BlockPos block) {
		final Vec3d[] eyesPos = new Vec3d[1];
		final BlockHitResult[] hitResult = new BlockHitResult[1];
		return ConfigManager.MC.world.getPlayers().parallelStream().filter(e -> e != ConfigManager.MC.player).filter(e -> e.isHolding(Items.END_CRYSTAL)).anyMatch(e -> {
			eyesPos[0] = RotationUtils.getEyesPos(e);
			hitResult[0] = ConfigManager.MC.world.raycast(new RaycastContext(eyesPos[0], eyesPos[0].add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity) e));
			return hitResult[0] != null && hitResult[0].getBlockPos().equals((Object)block);
		});
	}
}
