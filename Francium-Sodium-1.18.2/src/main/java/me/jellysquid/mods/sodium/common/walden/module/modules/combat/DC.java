package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import me.jellysquid.mods.sodium.common.walden.event.events.ItemUseListener;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.util.BlockUtils;
import me.jellysquid.mods.sodium.common.walden.util.CrystalUtils;
import me.jellysquid.mods.sodium.common.walden.util.RotationUtils;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class DC
        extends Module
        implements PlayerTickListener, ItemUseListener {
    private int CrystalCounter = 0;
    private int CrystalCounterClock = 0;
    private int CrystalOnceClock = 0;
    private int CrystalTwiceClock = 0;
    private final IntegerSetting breakInterval = IntegerSetting.Builder.newInstance().setName("Crystal Speed").setDescription("the interval between breaking crystals (in tick)").setModule(this).setValue(0).setMin(0).setMax(20).setAvailability(() -> true).build();
    private final IntegerSetting Cooldown = IntegerSetting.Builder.newInstance().setName("Cooldown").setDescription("Cooldown after Crystalling twice").setModule(this).setValue(20).setMin(1).setMax(200).setAvailability(() -> true).build();
    private final BooleanSetting activateOnRightClick = BooleanSetting.Builder.newInstance().setName("Use Right Click").setDescription("will only activate on right click when enabled").setModule(this).setValue(true).setAvailability(() -> true).build();
    private final IntegerSetting BreakTwice = IntegerSetting.Builder.newInstance().setName("BreakTwice").setDescription("Breaking the crystal twice").setModule(this).setValue(20).setMin(1).setMax(50).setAvailability(() -> true).build();
    private final BooleanSetting stopOnKill = BooleanSetting.Builder.newInstance().setName("Anti Loot Blow Up").setDescription("automatically stops crystalling when someone close to you dies").setModule(this).setValue(true).setAvailability(() -> true).build();
    private int crystalBreakClock = 0;

    public DC() {
        super("Double Crystal", "Double Crystal is a rewrite of MC", false, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
        eventManager.add(ItemUseListener.class, this);
        this.crystalBreakClock = 0;
        this.CrystalCounter = 0;
        this.CrystalCounterClock = 0;
        this.CrystalOnceClock = 0;
        this.CrystalTwiceClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        //eventManager = ConfigManager.INSTANCE.getEventManager();
        eventManager.remove(PlayerTickListener.class, this);
        eventManager.remove(ItemUseListener.class, this);
    }

    private boolean isDeadBodyNearby() {
        return ConfigManager.MC.world.getPlayers().parallelStream().filter(e -> ConfigManager.MC.player != e).filter(e -> e.squaredDistanceTo((Entity) ConfigManager.MC.player) < 36.0).anyMatch(LivingEntity::isDead);
    }

    /*@Override
    public void ItemUseListener(ItemUseEvent event) {
        ItemStack mainHandStack = ConfigManager.MC.player.getMainHandStack();
        if (ConfigManager.MC.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult)ConfigManager.MC.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos())) {
                event.cancel();
            }
        }
    }*/

    @Override
    public void onPlayerTick() {
        boolean dontBreakCrystal;
        boolean bl = dontBreakCrystal = this.crystalBreakClock != 0;
        if (this.CrystalCounter == 3) {
            ++this.CrystalCounterClock;
            if (this.CrystalCounterClock == this.Cooldown.get()) {
                this.CrystalCounter = 0;
                this.CrystalCounterClock = 0;
                return;
            }
            return;
        }
        if (this.CrystalCounter == 1) {
            ++this.CrystalOnceClock;
            if (this.CrystalOnceClock == this.Cooldown.get()) {
                this.CrystalCounter = 0;
                this.CrystalOnceClock = 0;
                return;
            }
        }
        if (this.CrystalCounter == 2) {
            ++this.CrystalTwiceClock;
            if (this.CrystalTwiceClock == this.Cooldown.get()) {
                this.CrystalCounter = 0;
                this.CrystalTwiceClock = 0;
                return;
            }
        }
        if (dontBreakCrystal) {
            --this.crystalBreakClock;
        }
        if (this.activateOnRightClick.get().booleanValue() && GLFW.glfwGetMouseButton((long) MC.getWindow().getHandle(), (int)1) != 1) {
            return;
        }
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL)) {
            return;
        }
        if (this.stopOnKill.get().booleanValue() && this.isDeadBodyNearby()) {
            return;
        }
        Vec3d camPos = MC.player.getEyePos();
        BlockHitResult blockHit = MC.world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity) MC.player));
        HitResult hitResult = MC.crosshairTarget;
        if (hitResult instanceof EntityHitResult) {
            Entity entity;
            EntityHitResult hit = (EntityHitResult)hitResult;
            if (!dontBreakCrystal && (hit.getEntity() instanceof EndCrystalEntity || hit.getEntity() instanceof SlimeEntity)) {
                this.crystalBreakClock = this.breakInterval.get();
                MC.interactionManager.attackEntity(MC.player, hit.getEntity());
                MC.player.swingHand(Hand.MAIN_HAND);
                ConfigManager.INSTANCE.getCrystalDataTracker().recordAttack(hit.getEntity());
            }
        }
        if (BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()) && CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
            ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, blockHit);
            ++this.CrystalCounter;
            if (result.isAccepted() && result.shouldSwingHand()) {
                MC.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    @Override
    public void onItemUse(ItemUseEvent event) {

    }

}
