package dev.client.tenacity.utils.player;

import dev.event.impl.player.MoveEvent;
import dev.utils.Utils;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.util.vector.Vector2f;

public class MovementUtils implements Utils {

    public static boolean isMoving() {
        if (mc.player == null) {
            return false;
        }
        return (mc.player.movementInput.field_192832_b != 0F || mc.player.movementInput.moveStrafe != 0F);
    }

    public static float getMoveYaw(float yaw) {
        Vector2f from = new Vector2f((float) mc.player.lastTickPosX, (float) mc.player.lastTickPosZ),
                to = new Vector2f((float) mc.player.posX, (float) mc.player.posZ),
                diff = new Vector2f(to.x - from.x, to.y - from.y);

        double x = diff.x, z = diff.y;
        if (x != 0 || z != 0) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        mc.player.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        mc.player.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, mc.player.rotationYaw, mc.player.movementInput.moveStrafe, mc.player.movementInput.field_192832_b);
    }

    public static void setSpeed(MoveEvent moveEvent, double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public static void setSpeed(MoveEvent moveEvent, double moveSpeed) {
        setSpeed(moveEvent, moveSpeed, mc.player.rotationYaw, mc.player.movementInput.moveStrafe, mc.player.movementInput.field_192832_b);
    }

    public static boolean isOnGround(double height) {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, -height, 0)).isEmpty();
    }

    public static float getSpeed() {
        if (mc.player == null || mc.world == null) return 0;
        return (float) Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

}
