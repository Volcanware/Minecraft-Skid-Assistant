package dev.client.tenacity.module.impl.render;

import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.utils.render.ColorUtil;
import dev.client.tenacity.utils.render.RenderUtil;
import dev.event.EventListener;
import dev.event.impl.render.Render3DEvent;
import dev.settings.impl.BooleanSetting;
import dev.utils.misc.MathUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import static org.lwjgl.opengl.GL11.*;

public class ChinaHat extends Module {

    private final BooleanSetting firstPerson = new BooleanSetting("Show in first person", false);

    public ChinaHat() {
        super("ChinaHat", Category.RENDER, "epic hat");
        this.addSettings(firstPerson);
    }

    private final EventListener<Render3DEvent> onRender3D = e -> {
        if (mc.player == null || mc.world == null || mc.player.isInvisible() || mc.player.isDead) return;
        if (!firstPerson.isEnabled() && mc.gameSettings.thirdPersonView == 0) return;

        double posX = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                posY = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                posZ = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;

        AxisAlignedBB axisalignedbb = mc.player.getEntityBoundingBox();
        double height = axisalignedbb.maxY - axisalignedbb.minY + 0.02,
                radius = axisalignedbb.maxX - axisalignedbb.minX;

        glPushMatrix();
        GlStateManager.disableCull();
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glDisable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_BLEND);
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        float yaw = MathUtils.interpolate(mc.player.prevRotationYaw, mc.player.rotationYaw, mc.timer.renderPartialTicks).floatValue();
        float pitchInterpolate = MathUtils.interpolate(mc.player.prevRenderArmPitch, mc.player.renderArmPitch, mc.timer.renderPartialTicks).floatValue();

        glTranslated(posX, posY, posZ);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glRotated(yaw, 0, -1, 0);
        glRotated(pitchInterpolate / 3.0, 0, 0, 0);
        glTranslatef(0, 0, pitchInterpolate / 270.0F);
        glLineWidth(2);
        glBegin(GL_LINE_LOOP);

        // outline/border or whatever you call it
        for (int i = 0; i <= 180; i++) {
            int color1 = ColorUtil.rainbow(7, i * 4, 1, 1, .5f).getRGB();
            GlStateManager.color(1, 1, 1, 1);
            RenderUtil.color(color1);
            glVertex3d(
                    posX - Math.sin(i * MathHelper.PI2 / 90) * radius,
                    posY + height - (mc.player.isSneaking() ? 0.23 : 0) - 0.002,
                    posZ + Math.cos(i * MathHelper.PI2 / 90) * radius
            );
        }
        glEnd();

        glBegin(GL_TRIANGLE_FAN);
        int color12 = ColorUtil.rainbow(7, 4, 1, 1, .7f).getRGB();
        RenderUtil.color(color12);
        glVertex3d(posX, posY + height + 0.3 - (mc.player.isSneaking() ? 0.23 : 0), posZ);

        // draw hat
        for (int i = 0; i <= 180; i++) {
            int color1 = ColorUtil.rainbow(7, i * 4, 1, 1, .2f).getRGB();
            GlStateManager.color(1, 1, 1, 1);
            RenderUtil.color(color1);
            glVertex3d(posX - Math.sin(i * MathHelper.PI2 / 90) * radius,
                    posY + height - (mc.player.isSneaking() ? 0.23F : 0),
                    posZ + Math.cos(i * MathHelper.PI2 / 90) * radius
            );

        }
        glVertex3d(posX, posY + height + 0.3 - (mc.player.isSneaking() ? 0.23 : 0), posZ);
        glEnd();


        glPopMatrix();

        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
    };

}
