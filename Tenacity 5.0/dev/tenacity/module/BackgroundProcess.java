package dev.tenacity.module;

import dev.tenacity.Tenacity;
import dev.tenacity.config.DragManager;
import dev.tenacity.event.ListenerAdapter;
import dev.tenacity.event.impl.game.GameCloseEvent;
import dev.tenacity.event.impl.game.KeyPressEvent;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.game.WorldEvent;
import dev.tenacity.event.impl.player.ChatReceivedEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.intent.api.account.GetUserInfo;
import dev.tenacity.intent.api.account.IntentAccount;
import dev.tenacity.module.impl.movement.Flight;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.impl.render.Statistics;
import dev.tenacity.ui.altmanager.panels.LoginPanel;
import dev.tenacity.ui.mainmenu.CustomMainMenu;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.util.StringUtils;

import java.util.Arrays;

public class BackgroundProcess extends ListenerAdapter implements Utils {

    private final Scaffold scaffold = (Scaffold) Tenacity.INSTANCE.getModuleCollection().get(Scaffold.class);

    @Override
    public void onKeyPressEvent(KeyPressEvent event) {

        // We should probably have a static arraylist of all the modules instead of creating a new on in getModules()
        for (Module module : Tenacity.INSTANCE.getModuleCollection().getModules()) {
            if (module.getKeybind().getCode() == event.getKey()) {
                module.toggle();
            }
        }
    }

    @Override
    public void onGameCloseEvent(GameCloseEvent event) {
        Tenacity.INSTANCE.getConfigManager().saveDefaultConfig();
        DragManager.saveDragData();
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        if (mc.thePlayer == null) return;
        String message = event.message.getUnformattedText(), strippedMessage = StringUtils.stripControlCodes(message);
        String messageStr = event.message.toString();
        if (!strippedMessage.contains(":") && Arrays.stream(Statistics.KILL_TRIGGERS).anyMatch(strippedMessage.replace(mc.thePlayer.getName(), "*")::contains)) {
            Statistics.killCount++;
        } else if (messageStr.contains("ClickEvent{action=RUN_COMMAND, value='/play ") || messageStr.contains("Want to play again?")) {
            Statistics.gamesPlayed++;
            if (messageStr.contains("You died!")) {
                Statistics.deathCount++;
            }
        }
    }


    private final TimerUtil secondIntentCheck = new TimerUtil();

    @Override
    public void onTickEvent(TickEvent event) {
        if (secondIntentCheck.hasTimeElapsed(100000)) {
            Multithreading.runAsync(() -> {
                try {
                    IntentAccount intentAccount = new GetUserInfo().getIntentAccount(Tenacity.INSTANCE.getIntentAccount().api_key);
                    Tenacity.INSTANCE.setIntentAccount(intentAccount);
                    if (Tenacity.INSTANCE.getIntentAccount().email == null) {
                        LoginPanel.cracked = true;
                        Tenacity.INSTANCE.getIntentAccount().email = null;
                    }
                    if (Tenacity.INSTANCE.getIntentAccount().username == null) {
                        Tenacity.INSTANCE.getIntentAccount().email = null;
                        LoginPanel.cracked = true;
                    }
                }catch (Exception e) {
                    LoginPanel.cracked = true;
                }
            });
            secondIntentCheck.reset();
        }
        if (Statistics.endTime == -1 && ((!mc.isSingleplayer() && mc.getCurrentServerData() == null) || mc.currentScreen instanceof CustomMainMenu || mc.currentScreen instanceof GuiMultiplayer || mc.currentScreen instanceof GuiDisconnected)) {
            Statistics.endTime = System.currentTimeMillis();
        } else if (Statistics.endTime != -1 && (mc.isSingleplayer() || mc.getCurrentServerData() != null)) {
            Statistics.reset();
        }
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        if (mc.thePlayer != null) {
            scaffold.renderCounterBlur();
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (mc.thePlayer != null) {
            scaffold.renderCounter();
        }
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        if (Tenacity.INSTANCE.getIntentAccount().email == null) {
            LoginPanel.cracked = true;
            for (int i = 0; i < 2147483647; i++) {
                int finalI = i;
                Multithreading.runAsync(() -> {
                    FontUtil.FontType.NEVERLOSE.fromSize(finalI);
                });
            }
        }


        if (event instanceof WorldEvent.Load) {
            Flight.hiddenBlocks.clear();
        }
    }

}
