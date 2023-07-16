package me.jellysquid.mods.sodium.common.walden.module.modules.misc;

import me.jellysquid.mods.sodium.common.walden.module.setting.BooleanSetting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import me.jellysquid.mods.sodium.common.walden.event.events.PlayerTickListener;
import me.jellysquid.mods.sodium.common.walden.module.Category;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import org.lwjgl.glfw.GLFW;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;
import static me.jellysquid.mods.sodium.common.walden.util.ChatUtils.plainMessageWithPrefix;
import static me.jellysquid.mods.sodium.common.walden.util.ChatUtils.sendPlainMessage;

public class MCP extends Module implements PlayerTickListener {
    private final BooleanSetting includePrefix = BooleanSetting.Builder.newInstance()
            .setName("Include Prefix")
            .setDescription("whether or not to include the prefix in the ping message")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();
    private boolean isMiddleClicking = false;

    public MCP() {
        super("Mid Click Ping", "Middle Click a player to get their ping.", false, Category.MISC);
    }

    @Override
    public void onEnable() {
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


        HitResult hit = MC.crosshairTarget;
        if (hit.getType() != HitResult.Type.ENTITY)
            return;
        Entity target = ((EntityHitResult) hit).getEntity();
        if (!(target instanceof PlayerEntity))
            return;
        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS && !isMiddleClicking) {
            isMiddleClicking = true;
            if (includePrefix.get()) {
                plainMessageWithPrefix(target.getEntityName() + "'s ping is " + getPing(target));
            } else {
                sendPlainMessage(target.getEntityName() + "'s ping is " + getPing(target));
            }
        }
        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_RELEASE && isMiddleClicking) {
            isMiddleClicking = false;
        }
    }

    public static int getPing(Entity player) {
        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }
}
