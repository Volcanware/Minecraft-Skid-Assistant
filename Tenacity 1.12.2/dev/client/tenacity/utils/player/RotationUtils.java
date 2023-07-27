package dev.client.tenacity.utils.player;

import dev.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils
implements Utils {
    public static void setRotations(float yaw, float pitch) {
        RotationUtils.mc.player.rotationYawHead = RotationUtils.mc.player.renderYawOffset = yaw;
        RotationUtils.mc.player.rotationPitch = pitch;
    }

    public static float clampRotation() {
        float rotationYaw = Minecraft.getMinecraft().player.rotationYaw;
        float n = 1.0f;
        if (Minecraft.getMinecraft().player.movementInput.field_192832_b < 0.0f) {
            rotationYaw += 180.0f;
            n = -0.5f;
        } else if (Minecraft.getMinecraft().player.movementInput.field_192832_b > 0.0f) {
            n = 0.5f;
        }
        if (Minecraft.getMinecraft().player.movementInput.moveStrafe > 0.0f) {
            rotationYaw -= 90.0f * n;
        }
        if (!(Minecraft.getMinecraft().player.movementInput.moveStrafe < 0.0f)) return rotationYaw * ((float)Math.PI / 180);
        rotationYaw += 90.0f * n;
        return rotationYaw * ((float)Math.PI / 180);
    }

    public static float getSensitivityMultiplier() {
        float SENSITIVITY = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6f + 0.2f;
        return SENSITIVITY * SENSITIVITY * SENSITIVITY * 8.0f * 0.15f;
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapDegrees(to - from);
        if (f > speed) {
            f = speed;
        }
        if (!(f < -speed)) return from + f;
        f = -speed;
        return from + f;
    }

    public static void setRotations(float[] rotations) {
        RotationUtils.setRotations(rotations[0], rotations[1]);
    }

    public static float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        double xSize = entity.posX - mc.player.posX;
        double ySize = entity.posY + (double)(entity.getEyeHeight() / 2.0f) - (mc.player.posY + (double)mc.player.getEyeHeight());
        double zSize = entity.posZ - mc.player.posZ;
        double theta = MathHelper.sqrt(xSize * xSize + zSize * zSize);
        float yaw = (float)(Math.atan2(zSize, xSize) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(ySize, theta) * 180.0 / Math.PI));
        return new float[]{(mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw)) % 360.0f, (mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)) % 360.0f};
    }

    public static float[] getFacingRotations2(int paramInt1, double d, int paramInt3) {
        EntitySnowball localEntityPig = new EntitySnowball(Minecraft.getMinecraft().world);
        localEntityPig.posX = (double)paramInt1 + 0.5;
        localEntityPig.posY = d + 0.5;
        localEntityPig.posZ = (double)paramInt3 + 0.5;
        return RotationUtils.getRotationsNeeded(localEntityPig);
    }

    public static float getYaw(Vec3d to) {
        float x = (float)(to.xCoord - RotationUtils.mc.player.posX);
        float z = (float)(to.zCoord - RotationUtils.mc.player.posZ);
        float var1 = (float)(StrictMath.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float rotationYaw = RotationUtils.mc.player.rotationYaw;
        return rotationYaw + MathHelper.wrapDegrees(var1 - rotationYaw);
    }

    public static Vec3d getVecRotations(float yaw, float pitch) {
        double d = Math.cos(Math.toRadians(-yaw) - Math.PI);
        double d1 = Math.sin(Math.toRadians(-yaw) - Math.PI);
        double d2 = -Math.cos(Math.toRadians(-pitch));
        double d3 = Math.sin(Math.toRadians(-pitch));
        return new Vec3d(d1 * d2, d3, d * d2);
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        double x = posX - RotationUtils.mc.player.posX;
        double z = posZ - RotationUtils.mc.player.posZ;
        double y = posY + (double)RotationUtils.mc.player.getEyeHeight() - RotationUtils.mc.player.posY;
        double d3 = MathHelper.sqrt(x * x + z * z);
        float yaw = (float)(MathHelper.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(MathHelper.atan2(y, d3) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }
}