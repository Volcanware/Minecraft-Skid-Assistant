package dev.zprestige.prestige.api.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static dev.zprestige.prestige.client.util.impl.RaytraceUtil.mc;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }


    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        float y = 2;
        float y2 = y + textRenderer.fontHeight + y;
        String space = " ";
        int spaceLength = textRenderer.getWidth(space);

        context.drawTextWithShadow(mc.textRenderer, "Prestige", 4, (int) y, 0x8376FC);
        context.drawTextWithShadow(mc.textRenderer, "Cracked by Volcan", 4, (int) y + 9, Color.white.getRGB());
    }
}
