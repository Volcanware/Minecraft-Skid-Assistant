package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TooltipProviderOptions implements TooltipProvider {
    public Rectangle getTooltipBounds(final GuiScreen guiScreen, final int x, final int y) {
        final int i = guiScreen.width / 2 - 150;
        int j = guiScreen.height / 6 - 7;

        if (y <= j + 98) {
            j += 105;
        }

        final int k = i + 150 + 150;
        final int l = j + 84 + 10;
        return new Rectangle(i, j, k - i, l - j);
    }

    public boolean isRenderBorder() {
        return false;
    }

    public String[] getTooltipLines(final GuiButton btn, final int width) {
        if (!(btn instanceof IOptionControl)) {
            return null;
        } else {
            final IOptionControl ioptioncontrol = (IOptionControl) btn;
            final GameSettings.Options gamesettings$options = ioptioncontrol.getOption();
            final String[] astring = getTooltipLines(gamesettings$options.getEnumString());
            return astring;
        }
    }

    public static String[] getTooltipLines(final String key) {
        final List<String> list = new ArrayList();

        for (int i = 0; i < 10; ++i) {
            final String s = key + ".tooltip." + (i + 1);
            final String s1 = Lang.get(s, null);

            if (s1 == null) {
                break;
            }

            list.add(s1);
        }

        if (list.size() <= 0) {
            return null;
        } else {
            final String[] astring = list.toArray(new String[list.size()]);
            return astring;
        }
    }
}
