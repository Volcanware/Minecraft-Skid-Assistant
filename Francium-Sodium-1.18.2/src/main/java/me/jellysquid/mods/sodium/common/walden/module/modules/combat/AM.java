package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.util.AnchorBlockUtils;
import me.jellysquid.mods.sodium.common.walden.util.InventoryUtils;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class AM extends Module implements PlayerTickListener
{
    private final IntegerSetting ExplodeSlot;
    private final BooleanSetting chargeOnly;
    private final IntegerSetting Cooldown;
    private boolean hasAnchored;
    private int clock;

    public AM() {
        super("Anchor Macro", "Automatically explodes Anchors you place", false, Category.COMBAT);
        this.ExplodeSlot = new IntegerSetting.Builder().setName("Exploding Slot").setDescription("which slot to switch to when exploding Anchors").setModule(this).setValue(0).setMin(0).setMax(8).setAvailability(() -> true).build();
        this.chargeOnly = BooleanSetting.Builder.newInstance().setName("Charge Only").setDescription("if on, It wont explode Anchors but just charge them").setModule(this).setValue(false).setAvailability(() -> true).build();
        this.Cooldown = new IntegerSetting.Builder().setName("Cooldown").setDescription("Cooldown").setModule(this).setValue(4).setMin(0).setMax(10).setAvailability(() -> true).build();
        this.hasAnchored = false;
        this.clock = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        AM.eventManager.add(PlayerTickListener.class, this);
        this.clock = 0;
        this.hasAnchored = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        AM.eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void onPlayerTick() {
        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), 1) != 1) {
            return;
        }
        if (MC.player.isUsingItem()) {
            return;
        }
        if (this.hasAnchored) {
            if (this.clock != 0) {
                --this.clock;
                return;
            }
            this.clock = this.Cooldown.get();
            this.hasAnchored = false;
        }
        final HitResult cr = MC.crosshairTarget;
        if (cr instanceof BlockHitResult) {
            final BlockHitResult hit = (BlockHitResult)cr;
            final BlockPos pos = hit.getBlockPos();
            if (AnchorBlockUtils.isAnchorUncharged(pos)) {
                if (MC.player.isHolding(Items.GLOWSTONE)) {
                    final ActionResult actionResult = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                    if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                        MC.player.swingHand(Hand.MAIN_HAND);
                    }
                    return;
                }
                InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
                final ActionResult actionResult = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                    MC.player.swingHand(Hand.MAIN_HAND);
                }
            }
            else if (AnchorBlockUtils.isAnchorCharged(pos) && !this.chargeOnly.get()) {
                final PlayerInventory inv = AM.mc.player.getInventory();
                inv.selectedSlot = this.ExplodeSlot.get();
                final ActionResult actionResult2 = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                if (actionResult2.isAccepted() && actionResult2.CONSUME.shouldSwingHand()) {
                    MC.player.swingHand(Hand.MAIN_HAND);
                }
                this.hasAnchored = true;
            }
        }
    }
}
