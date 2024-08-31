package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.event.events.KeyPressListener;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.keybind.Keybind;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.KeybindSetting;
import me.jellysquid.mods.sodium.common.walden.util.BlockUtils;
import me.jellysquid.mods.sodium.common.walden.util.InventoryUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class AMC extends Module implements PlayerTickListener, KeyPressListener {

    public AMC() {
        super("Auto MineCart", "auto minecart bruh", false, Category.COMBAT);
    }

    private IntegerSetting bowCharge = IntegerSetting.Builder.newInstance()
            .setName("Bow Charge")
            .setDescription("interval")
            .setModule(this)
            .setValue(5)
            .setMin(5)
            .setMax(20)
            .setAvailability(() -> true)
            .build();

    private IntegerSetting shootInterval = IntegerSetting.Builder.newInstance()
            .setName("Shoot Interval")
            .setDescription("interval")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(5)
            .setAvailability(() -> true)
            .build();

    private IntegerSetting placeMinecartInterval = IntegerSetting.Builder.newInstance()
            .setName("Place Minecart Interval")
            .setDescription("interval")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(5)
            .setAvailability(() -> true)
            .build();

    public final KeybindSetting activateKey = new KeybindSetting.Builder()
            .setName("Activate Key")
            .setDescription("the key to activate it")
            .setModule(this)
            .setValue(new Keybind("", GLFW.GLFW_KEY_C,false,false,null))
            .build();

    private int minecartPlaceClock;
    private int shootDelay;
    private int cnt;

    private boolean isThatRails;
    private boolean needToPlaceRails;
    private boolean shooted;
    private boolean needToShoot;
    private boolean needToPlaceMinecart;

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
        eventManager.add(KeyPressListener.class, this);

        minecartPlaceClock = 0;
        shootDelay = 0;
        cnt = 0;

        isThatRails = false;
        needToPlaceMinecart = false;
        needToPlaceRails = false;
        needToShoot = false;
        shooted = false;
    }

    private boolean check() {
        return isThatRails || needToPlaceMinecart || needToPlaceRails || needToShoot || shooted;
    }

    private boolean checkHotBar() {
        return InventoryUtils.hasItemInHotbar(Items.TNT_MINECART) && InventoryUtils.hasItemInHotbar(Items.RAIL)
                && InventoryUtils.hasItemInHotbar(Items.BOW);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        eventManager.remove(PlayerTickListener.class, this);
        eventManager.remove(KeyPressListener.class, this);
    }

    @Override
    public void onPlayerTick() {

        if (GLFW.glfwGetKey(MC.getWindow().getHandle(), activateKey.get().getKey()) != GLFW.GLFW_PRESS) {
            isThatRails = false;
            needToPlaceMinecart = false;
            needToPlaceRails = false;
            needToShoot = false;
            shooted = false;
            cnt = 0;
            return;
        }

        if (cnt == 0) {
            if (needToPlaceRails) {

                if (MC.crosshairTarget instanceof BlockHitResult hit
                        && !BlockUtils.isBlock(Blocks.AIR, hit.getBlockPos())) {
                    InventoryUtils.selectItemFromHotbar(Items.RAIL);
                    ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                    if (result.isAccepted() && result.shouldSwingHand()) MC.player.swingHand(Hand.MAIN_HAND);
                }

                needToPlaceMinecart = false;
                needToPlaceRails = false;
                needToShoot = false;
                shooted = false;
                isThatRails = true;
            }

            if (isThatRails && shootDelay != shootInterval.get()) {
                shootDelay++;
                return;
            } else if (isThatRails) {
                shootDelay = 0;
                isThatRails = false;
                needToPlaceMinecart = false;
                needToPlaceRails = false;
                shooted = false;
                needToShoot = true;
            }

            if (needToShoot) {

                InventoryUtils.selectItemFromHotbar(Items.BOW);

                if (MC.player.getItemUseTime() >= bowCharge.get()) {
                    MC.player.stopUsingItem();
                    MC.interactionManager.stopUsingItem(MC.player);
                    MC.options.useKey.setPressed(false);

                    isThatRails = false;
                    needToPlaceMinecart = false;
                    needToPlaceRails = false;
                    needToShoot = false;
                    shooted = true;
                } else {
                    MC.options.useKey.setPressed(true);
                }

            }

            if (shooted && minecartPlaceClock != placeMinecartInterval.get()) {
                minecartPlaceClock++;
                return;
            } else if (shooted) {
                minecartPlaceClock = 0;
                isThatRails = false;
                needToPlaceRails = false;
                needToShoot = false;
                shooted = false;
                needToPlaceMinecart = true;
            }

            if (needToPlaceMinecart) {

                if (MC.crosshairTarget instanceof BlockHitResult hit
                        && BlockUtils.isBlock(Blocks.RAIL, hit.getBlockPos())) {
                    InventoryUtils.selectItemFromHotbar(Items.TNT_MINECART);
                    ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                    if (result.isAccepted() && result.shouldSwingHand())
                        MC.player.swingHand(Hand.MAIN_HAND);

                }

                isThatRails = false;
                needToPlaceMinecart = false;
                needToPlaceRails = false;
                needToShoot = false;
                shooted = false;
                cnt++;
            }
        }

    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        if (event.getKeyCode() == activateKey.get().getKey() && !check() && checkHotBar()) {
            if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hit = (BlockHitResult) MC.crosshairTarget;
                if (BlockUtils.isBlock(Blocks.RAIL, hit.getBlockPos())) {
                    isThatRails = true;
                } else if (!BlockUtils.isBlock(Blocks.AIR, hit.getBlockPos())) {
                    needToPlaceRails = true;
                }
            }
        }
    }
}
