package dev.client;

import dev.client.Client;
import dev.client.ClientType;
import dev.client.tenacity.Tenacity;
import dev.client.tenacity.module.TenacityBackgroundProcess;
import dev.client.tenacity.ui.mainmenu.TenacityMainMenu;
import dev.client.tenacity.utils.misc.HoveringUtil;
import dev.client.tenacity.utils.render.ColorUtil;
import dev.client.tenacity.utils.render.RenderUtil;
import dev.client.tenacity.utils.render.RoundedUtil;
import dev.client.tenacity.utils.render.ShaderUtil;
import dev.utils.animations.Animation;
import dev.utils.animations.Direction;
import dev.utils.font.FontUtil;
import dev.utils.render.StencilUtil;
import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class EnterScreen
extends GuiScreen {
    private Animation hoverTenaAnimation;
    private Animation hoverRoseAnimation;
    private final long startTime = 0L;

    @Override
    public void initGui() {
        this.mc.gameSettings.ofFastRender = false;
        this.loadTenacity();
        this.mc.displayGuiScreen(new TenacityMainMenu());
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode != 1) return;
        this.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect2(0.0, 0.0, this.width, this.height, new Color(30, 30, 30).getRGB());
        FontUtil.tenacityFont40.drawCenteredString("What client would you like to play?", (float)this.width / 2.0f, 50.0f, -1);
        ScaledResolution sr = new ScaledResolution(this.mc);
        float buttonWH = 200.0f;
        float tenaX = (float)sr.getScaledWidth() / 2.0f - buttonWH / 2.0f - 170.0f;
        float tenaY = (float)sr.getScaledHeight() / 2.0f - buttonWH / 2.0f;
        boolean hoveringTena = HoveringUtil.isHovering(tenaX, tenaY, buttonWH, buttonWH, mouseX, mouseY);
        this.hoverTenaAnimation.setDirection(hoveringTena ? Direction.FORWARDS : Direction.BACKWARDS);
        Color tena1 = ColorUtil.interpolateColorsBackAndForth(15, 1, Tenacity.INSTANCE.getClientColor(), Tenacity.INSTANCE.getAlternateClientColor(), false);
        Color tena2 = ColorUtil.interpolateColorsBackAndForth(15, 1, Tenacity.INSTANCE.getAlternateClientColor(), Tenacity.INSTANCE.getClientColor(), false);
        float tenaMovement = (float)(35.0 * this.hoverTenaAnimation.getOutput());
        FontUtil.tenacityFont40.drawCenteredString("Play Tenacity?", tenaX + buttonWH / 2.0f, tenaY + buttonWH - (float)FontUtil.tenacityFont40.getHeight(), ColorUtil.applyOpacity(-1, (float)this.hoverTenaAnimation.getOutput()));
        RoundedUtil.drawGradientCornerLR(tenaX, tenaY - tenaMovement, buttonWH, buttonWH, 30.0f, tena1, tena2);
        FontUtil.tenacityBoldFont40.drawCenteredString("Tenacity 4.0", tenaX + buttonWH / 2.0f, tenaY + buttonWH - (float)(40 + FontUtil.tenacityBoldFont40.getHeight()) - tenaMovement, -1);
        FontUtil.tenacityFont20.drawCenteredString("by cedo, tear, senoe, and fathum", tenaX + buttonWH / 2.0f, tenaY + buttonWH - (float)(10 + FontUtil.tenacityBoldFont40.getHeight()) - tenaMovement, -1);
        GL11.glEnable((int)3042);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/enterScreen.png"));
        float tenaLogoWH = 126.5f;
        EnterScreen.drawModalRectWithCustomSizedTexture(tenaX + buttonWH / 2.0f - tenaLogoWH / 2.0f, tenaY + 10.0f - tenaMovement, 0.0f, 0.0f, tenaLogoWH, tenaLogoWH, tenaLogoWH, tenaLogoWH);
        float roseX = (float)sr.getScaledWidth() / 2.0f - buttonWH / 2.0f + 170.0f;
        float roseY = (float)sr.getScaledHeight() / 2.0f - buttonWH / 2.0f;
        boolean hoveringRose = HoveringUtil.isHovering(roseX, roseY, buttonWH, buttonWH, mouseX, mouseY);
        this.hoverRoseAnimation.setDirection(hoveringRose ? Direction.FORWARDS : Direction.BACKWARDS);
        FontUtil.tenacityFont40.drawCenteredString("Play Rose?", roseX + buttonWH / 2.0f, roseY + buttonWH - (float)FontUtil.tenacityFont40.getHeight(), ColorUtil.applyOpacity(-1, (float)this.hoverRoseAnimation.getOutput()));
        float roseMovement = (float)(35.0 * this.hoverRoseAnimation.getOutput());
        RenderUtil.setAlphaLimit(0.0f);
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(roseX, roseY - roseMovement, buttonWH, buttonWH, 30.0f, Tenacity.INSTANCE.getClientColor());
        StencilUtil.readStencilBuffer(1);
        ShaderUtil.drawQuads(roseX - 25.0f, roseY - (25.0f + roseMovement), buttonWH + 50.0f, buttonWH + 50.0f);
        StencilUtil.uninitStencilBuffer();
        GL11.glEnable((int)3042);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Rose/roseEnter.png"));
        float roseLogoWidth = 101.5f;
        float roseLogoHeight = 128.0f;
        EnterScreen.drawModalRectWithCustomSizedTexture(roseX + buttonWH / 2.0f - roseLogoWidth / 2.0f, roseY + 10.0f - roseMovement, 0.0f, 0.0f, roseLogoWidth, roseLogoHeight, roseLogoWidth, roseLogoHeight);
        FontUtil.tenacityFont20.drawCenteredString("by trq, aether, g8lol, and cedo", roseX + buttonWH / 2.0f, roseY + buttonWH - (float)(10 + FontUtil.tenacityBoldFont40.getHeight()) - roseMovement, -1);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(this.mc);
        float buttonWH = 200.0f;
        float tenaX = (float)sr.getScaledWidth() / 2.0f - buttonWH / 2.0f - 170.0f;
        float tenaY = (float)sr.getScaledHeight() / 2.0f - buttonWH / 2.0f;
        float roseMovement = (float)(35.0 * this.hoverRoseAnimation.getOutput());
        float roseX = (float)sr.getScaledWidth() / 2.0f - buttonWH / 2.0f + 170.0f;
        float roseY = (float)sr.getScaledHeight() / 2.0f - buttonWH / 2.0f;
        boolean hoveringTena = HoveringUtil.isHovering(tenaX, tenaY, buttonWH, buttonWH, mouseX, mouseY);
        boolean hoveringRose = HoveringUtil.isHovering(roseX, roseY, buttonWH, buttonWH, mouseX, mouseY);
        if (mouseButton != 0) return;
        if (hoveringTena) {
            this.loadTenacity();
            this.mc.displayGuiScreen(new TenacityMainMenu());
        } else {
            if (!hoveringRose) return;
            Client.client = ClientType.ROSE;
        }
    }

    private void loadTenacity() {
        Display.setTitle((String)("Tenacity " + Tenacity.INSTANCE.getVersion()));
        Tenacity.INSTANCE.getEventProtocol().register(new TenacityBackgroundProcess());
        Thread configDaemon = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Tenacity.INSTANCE.getConfigManager().saveDefaultConfig();
            }
        });
        configDaemon.setDaemon(true);
        configDaemon.start();
        Client.client = ClientType.TENACITY;
    }
}