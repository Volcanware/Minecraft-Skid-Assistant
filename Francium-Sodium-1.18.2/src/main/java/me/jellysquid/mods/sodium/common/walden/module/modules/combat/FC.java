package me.jellysquid.mods.sodium.common.walden.module.modules.combat;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.event.events.ItemUseListener;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.IntegerSetting;
import me.jellysquid.mods.sodium.common.walden.util.BlockUtils;
import me.jellysquid.mods.sodium.common.walden.util.CrystalUtils;
import me.jellysquid.mods.sodium.common.walden.util.RotationUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class FC extends Module implements ItemUseListener, PlayerTickListener {
    public FC() {
        super("Fast Crystals", "make crystal placing fast", false, Category.COMBAT);
    }

    private final IntegerSetting placeInterval = IntegerSetting.Builder.newInstance()
            .setName("Place interval")
            .setDescription("the delay between placing crystals (in tick)")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(20)
            .setAvailability(() -> true)
            .build();

    private int delay = 0;

    @Override
    public void onEnable()
    {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
        eventManager.add(ItemUseListener.class, this);

        delay = 0;
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        EventManager eventManager = ConfigManager.INSTANCE.getEventManager();
        eventManager.remove(PlayerTickListener.class, this);
        eventManager.remove(ItemUseListener.class, this);
    }

    @Override
    public void onItemUse(ItemUseEvent event)
    {
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockHitResult hit = (BlockHitResult) MC.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) &&
                    (BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()) ||
                            BlockUtils.isBlock(Blocks.BEDROCK, hit.getBlockPos()))) {
                event.cancel();
            }
        }
    }

    @Override
    public void onPlayerTick() {

        if (!MC.player.getMainHandStack().isOf(Items.END_CRYSTAL)) { return; }

        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS) { return; }

        if (delay != placeInterval.get()) {
            delay++;
        } else {
            delay = 0;
        }

        Vec3d camPos = MC.player.getEyePos();
        BlockHitResult blockHit = MC.world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, MC.player));

        if (BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()) || BlockUtils.isBlock(Blocks.BEDROCK, blockHit.getBlockPos())) {
            if (CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
                ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, blockHit);
                if (result.isAccepted() && result.shouldSwingHand()) { MC.player.swingHand(Hand.MAIN_HAND); }
            }
        }

    }
}
