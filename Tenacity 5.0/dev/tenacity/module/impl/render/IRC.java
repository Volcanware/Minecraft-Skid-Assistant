package dev.tenacity.module.impl.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.player.PlayerSendMessageEvent;
import dev.tenacity.intent.irc.IRCUtil;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;

public class IRC extends Module {

    public static final BooleanSetting shareUsername = new BooleanSetting("Share MC Username", true);

    public static boolean filter = false;

    public IRC() {
        super("IRC", Category.RENDER, "Internet Relay Chat for Tenacity and Intent client users");
        addSettings(shareUsername);
        this.setToggled(true);
    }


    @Override
    public void onTickEvent(TickEvent event) {
        Tenacity.INSTANCE.getIrcUtil().onTick();
    }

    @Override
    public void onPlayerSendMessageEvent(PlayerSendMessageEvent event) {
        if (event.getMessage().startsWith("-")) {
            event.cancel();
            Tenacity.INSTANCE.getIrcUtil().onMessage(event.getMessage().substring(1));
        }
    }

    public static String filter(String text) {
        if (filter) {
            for (String user : IRCUtil.usersMap.keySet()) {
                if (text.contains(user)) {
                    String ircName = " §7(§d" + IRCUtil.usersMap.get(user) + "§7)§r";
                    return text.replace(user, user + ircName);
                }
            }
        }
        return text;
    }


    @Override
    public void onDisable() {
        toggle();
    }

}
