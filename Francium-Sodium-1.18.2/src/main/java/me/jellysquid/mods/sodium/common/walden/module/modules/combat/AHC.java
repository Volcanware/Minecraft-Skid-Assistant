package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.keybind.Keybind;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.KeybindSetting;
import me.jellysquid.mods.sodium.common.walden.util.CrystalUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.util.BlockUtils;
import me.jellysquid.mods.sodium.common.walden.util.InventoryUtils;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class AHC extends Module implements PlayerTickListener {

    public AHC() {
        super("Auto Hit Crystal", "Credits to pycat", false, Category.COMBAT);
    }


    private IntegerSetting firstCrystalInterval = IntegerSetting.Builder.newInstance()
            .setName("First Crystal Interval")
            .setDescription("interval of first crystal place after placing obsidian (in tick)")
            .setModule(this)
            .setValue(5)
            .setMin(0)
            .setMax(10)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting placeInterval = IntegerSetting.Builder.newInstance()
            .setName("Place Interval")
            .setDescription("the interval between placing crystals (in tick)")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(20)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting breakInterval = IntegerSetting.Builder.newInstance()
            .setName("Break Interval")
            .setDescription("the interval between breaking crystals (in tick)")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(20)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting stopOnKill = BooleanSetting.Builder.newInstance()
            .setName("Stop On Kill")
            .setDescription("automatically stops crystalling when someone close to you dies")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting workOnKeybind = BooleanSetting.Builder.newInstance()
            .setName("Work On Keybind?")
            .setDescription("work on keybind")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting workWithTotem = BooleanSetting.Builder.newInstance()
            .setName("Work With Totem")
            .setDescription("work with totem")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    public final KeybindSetting activateKey = new KeybindSetting.Builder()
            .setName("Keybind")
            .setDescription("the key to activate it")
            .setModule(this)
            .setValue(new Keybind("", GLFW.GLFW_KEY_R,false,false,null))
            .build();

    private int crystalPlaceClock = 0;
    private int crystalBreakClock = 0;
    private int firstCrystalDelay = 0;

    private boolean placedObby;
    private boolean crystalStatus;

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);

        crystalPlaceClock = 0;
        crystalBreakClock = 0;
        firstCrystalDelay = 0;

        placedObby = false;
        crystalStatus = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }

    private boolean check() {
        ItemStack mainHand = MC.player.getMainHandStack();

        return  !workOnKeybind.get()
                && (mainHand.isOf(Items.NETHERITE_SWORD)
                || mainHand.isOf(Items.DIAMOND_SWORD)
                || mainHand.isOf(Items.GOLDEN_SWORD)
                || mainHand.isOf(Items.IRON_SWORD)
                || mainHand.isOf(Items.STONE_SWORD)
                || mainHand.isOf(Items.WOODEN_SWORD)
                || workWithTotem.get() && mainHand.isOf(Items.TOTEM_OF_UNDYING));
    }


    private boolean isDeadBodyNearby()
    {
        return MC.world.getPlayers().parallelStream()
                .filter(e -> MC.player != e)
                .filter(e -> e.squaredDistanceTo(MC.player.getPos()) < 50)
                .anyMatch(PlayerEntity::isDead);

    }


    @Override
    public void onPlayerTick() {

        if (MC.currentScreen != null)
            return;

        if (GLFW.glfwGetKey(MC.getWindow().getHandle(), activateKey.get().getKey()) != GLFW.GLFW_PRESS && workOnKeybind.get()) {
            firstCrystalDelay = 0;
            placedObby = false;
            crystalStatus = false;
            return;
        }

        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS && !workOnKeybind.get()) {
            firstCrystalDelay = 0;
            placedObby = false;
            crystalStatus = false;
            return;
        }

        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS
                && !workOnKeybind.get()
                && !crystalStatus
                && !check())
            return;

        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS && check())
            MC.options.useKey.setPressed(false);


        if (firstCrystalDelay != firstCrystalInterval.get()
                && !placedObby
                && MC.crosshairTarget instanceof BlockHitResult hit
                && !(BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos())
                || BlockUtils.isBlock(Blocks.AIR, hit.getBlockPos()))) {


            if (InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN)) {
                ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                if (result.isAccepted() && result.shouldSwingHand()) {
                    MC.player.swingHand(Hand.MAIN_HAND);
                    placedObby = true;
                }
            }

        }

        if (firstCrystalDelay != firstCrystalInterval.get()) {
            firstCrystalDelay++;
            crystalStatus = true;
            return;
        }

        if (firstCrystalDelay == firstCrystalInterval.get()
                && ((MC.crosshairTarget instanceof EntityHitResult hitEntity
                && (hitEntity.getEntity() instanceof EndCrystalEntity || hitEntity.getEntity() instanceof SlimeEntity))
                || (MC.crosshairTarget instanceof BlockHitResult hit
                && BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos())))) {
            boolean dontPlaceCrystal = crystalPlaceClock != 0;
            boolean dontBreakCrystal = crystalBreakClock != 0;


            if (dontPlaceCrystal)
                crystalPlaceClock--;
            if (dontBreakCrystal)
                crystalBreakClock--;

            if (stopOnKill.get() && isDeadBodyNearby()) {
                return;
            }

            InventoryUtils.selectItemFromHotbar(Items.END_CRYSTAL);

            if (MC.crosshairTarget instanceof EntityHitResult hit)
            {
                if (!dontBreakCrystal && (hit.getEntity() instanceof EndCrystalEntity || hit.getEntity() instanceof SlimeEntity))
                {
                    crystalBreakClock = breakInterval.get();
                    MC.interactionManager.attackEntity(MC.player, hit.getEntity());
                    MC.player.swingHand(Hand.MAIN_HAND);
                    ConfigManager.INSTANCE.getCrystalDataTracker().recordAttack(hit.getEntity());
                }
            }
            if (MC.crosshairTarget instanceof BlockHitResult hit)
            {
                BlockPos block = hit.getBlockPos();
                if (!dontPlaceCrystal && CrystalUtils.canPlaceCrystalServer(block))
                {
                    crystalPlaceClock = placeInterval.get();
                    ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                    if (result.isAccepted() && result.shouldSwingHand())
                        MC.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
}