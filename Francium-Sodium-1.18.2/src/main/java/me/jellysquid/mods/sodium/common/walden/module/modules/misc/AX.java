package me.jellysquid.mods.sodium.common.walden.module.modules.misc;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import me.jellysquid.mods.sodium.common.walden.module.setting.DecimalSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;

public class AX extends Module
        implements PlayerTickListener {
    private int DropClock = 0;
    private final BooleanSetting ActivateOnRightClick = BooleanSetting.Builder.newInstance().setName("Activate On Right Click").setDescription("When deactivated, XP will also splash in Inventory Screen").setModule(this).setValue(false).setAvailability(() -> true).build();
    private final BooleanSetting OnlyMainScreen = BooleanSetting.Builder.newInstance().setName("MainList Screen Only").setDescription("When deactivated, XP will also splash in Inventory Screen").setModule(this).setValue(false).setAvailability(() -> true).build();
    private final DecimalSetting speed = DecimalSetting.Builder.newInstance().setName("Speed").setDescription("Dropping Speed").setModule(this).setValue(1.0).setMin(1.0).setMax(10.0).setStep(1.0).setAvailability(() -> true).build();

    public AX() {
        super("Auto XP", "automatically splashes XP When you hold them", false, Category.MISC);
    }

    @Override
    public void onEnable() {
        this.DropClock = 0;
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void onPlayerTick() {
        if (ConfigManager.MC.currentScreen != null && this.OnlyMainScreen.get()) {
            return;
        }
        if (this.ActivateOnRightClick.get() && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS) {
            return;
        }
        ++this.DropClock;
        if ((double)this.DropClock != this.speed.get()+1) {
            return;
        }
        this.DropClock = 0;
        ItemStack mainHandStack = ConfigManager.MC.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.EXPERIENCE_BOTTLE)) {
            return;
        }
        ConfigManager.MC.interactionManager.interactItem((PlayerEntity) ConfigManager.MC.player, MC.world, Hand.MAIN_HAND);
        ConfigManager.MC.player.swingHand(Hand.MAIN_HAND);
    }
}