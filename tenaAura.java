package volcanware.anarchyclef.altomenu.modules.Combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import volcanware.anarchyclef.altomenu.Mod;
import volcanware.anarchyclef.altomenu.modules.Render.Animations;
import volcanware.anarchyclef.altomenu.settings.BooleanSetting;
import volcanware.anarchyclef.altomenu.settings.ModeSetting;
import volcanware.anarchyclef.altomenu.settings.NumberSetting;
import volcanware.anarchyclef.eventbus.EventHandler;
import volcanware.anarchyclef.eventbus.events.PacketEvent;
import volcanware.anarchyclef.mixinManager.RotationManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class tenaAura extends Mod {
    public static tenaAura INSTANCE = new tenaAura();

    // Static state
    public static boolean attacking = false;
    public static boolean blocking = false;
    public static boolean wasBlocking = false;
    public static boolean fakeBlocking = false;
    public static LivingEntity target = null;
    public static final List<LivingEntity> targets = new ArrayList<>();
    public static float RTYAW = 0;
    public static float RTPITCH = 0;
    public static boolean HasTarget = false;
    private boolean replacingPacket = false;

    // Timers
    private long attackTimer = 0;
    private long switchTimer = 0;
    private float yaw = 0;
    private float pitch = 0;
    private int cps = 0;
    private final Random random = new Random();

    // Target settings
    private final BooleanSetting targetPlayers = new BooleanSetting("Target Players", true);
    private final BooleanSetting targetAnimals = new BooleanSetting("Target Animals", false);
    private final BooleanSetting targetMobs = new BooleanSetting("Target Mobs", false);
    private final BooleanSetting targetInvisibles = new BooleanSetting("Target Invisibles", false);

    // Mode settings
    private final ModeSetting mode = new ModeSetting("Mode", "Single", "Single", "Multi");
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 50, 500, 100, 10);
    private final NumberSetting maxTargetAmount = new NumberSetting("Max Targets", 3, 50, 2, 1);

    // Attack settings
    private final NumberSetting minCPS = new NumberSetting("Min CPS", 5, 20, 8, 1);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 5, 20, 12, 1);
    private final NumberSetting reach = new NumberSetting("Reach", 3.0, 6.0, 4.5, 0.1);

    // Autoblock settings
    private final BooleanSetting autoblock = new BooleanSetting("Autoblock", false);
    private final ModeSetting autoblockMode = new ModeSetting("Autoblock Mode", "Watchdog", "Watchdog", "Fake", "Verus");

    // Rotation settings
    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Packet", "Client", "Packet", "Smooth");
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation Speed", 1.0, 10.0, 5.0, 0.5);

    // Sort mode
    private final ModeSetting sortMode = new ModeSetting("Sort Mode", "Range", "Range", "Hurt Time", "Health", "Armor");

    // Addons
    private final BooleanSetting keepSprint = new BooleanSetting("Keep Sprint", true);
    private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", true);
    private final BooleanSetting rayCast = new BooleanSetting("Ray Cast", false);
    private final BooleanSetting movementFix = new BooleanSetting("Movement Fix", false);

    public tenaAura() {
        super("tenaAura", "Tenacity KillAura port", Category.COMBAT);
        INSTANCE = this;

        // Parent-child relationships
        switchDelay.withCondition(() -> mode.isMode("Multi"));
        maxTargetAmount.withCondition(() -> mode.isMode("Multi"));
        autoblockMode.withCondition(autoblock::isEnabled);
        rotationMode.withCondition(rotations::isEnabled);
        rotationSpeed.withCondition(() -> rotations.isEnabled() && rotationMode.isMode("Smooth"));

        addSettings(
            targetPlayers, targetAnimals, targetMobs, targetInvisibles,
            mode, switchDelay, maxTargetAmount,
            minCPS, maxCPS, reach,
            autoblock, autoblockMode,
            rotations, rotationMode, rotationSpeed,
            sortMode,
            keepSprint, throughWalls, rayCast, movementFix
        );

        PacketEvent.addGlobalListener(this::onPacket);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        targets.clear();
        target = null;
        blocking = false;
        attacking = false;
        wasBlocking = false;
        fakeBlocking = false;
        attackTimer = System.currentTimeMillis();
        switchTimer = System.currentTimeMillis();
        yaw = mc.player != null ? mc.player.getYaw() : 0f;
        pitch = mc.player != null ? mc.player.getPitch() : 0f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        targets.clear();
        target = null;
        blocking = false;
        attacking = false;
        fakeBlocking = false;
        HasTarget = false;
        RotationManager.isUnlocked = false;
        if (wasBlocking) {
            unblock();
        }
        wasBlocking = false;
        Animations.animate = false;
    }

    private void onPacket(PacketEvent evt) {
        // Prevent infinite loop - skip if we're already replacing a packet
        if (replacingPacket) return;

        // Only care about outgoing packets
        if (evt.direction != PacketEvent.Direction.SEND) return;

        // Check if it's a movement packet with rotation
        if (!(evt.packet instanceof PlayerMoveC2SPacket packet)) return;

        // Check if packet has rotation data
        if (!(packet instanceof PlayerMoveC2SPacket.LookAndOnGround) &&
            !(packet instanceof PlayerMoveC2SPacket.Full)) return;

        // Only replace if enabled, targeting, and in Packet or Smooth mode
        if (!isEnabled() || !HasTarget || !rotations.isEnabled() || rotationMode.isMode("Client")) return;

        // Cancel original packet
        evt.cancel();

        // Set flag to prevent re-entrancy
        replacingPacket = true;

        // Send packet with preserved position/velocity but modified rotations
        if (packet instanceof PlayerMoveC2SPacket.Full fullPacket) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                fullPacket.getX(0), fullPacket.getY(0), fullPacket.getZ(0),
                RTYAW, RTPITCH, fullPacket.isOnGround()
            ));
        } else {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(RTYAW, RTPITCH, mc.player.isOnGround()));
        }

        // Reset flag
        replacingPacket = false;
    }

    @EventHandler
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Ensure minCPS <= maxCPS
        if (minCPS.getValue() > maxCPS.getValue()) {
            minCPS.setValue(maxCPS.getValue());
        }

        // Sort targets
        sortTargets();

        attacking = !targets.isEmpty();
        HasTarget = attacking;

        if (attacking) {
            target = targets.get(0);

            // Handle rotations
            if (rotations.isEnabled()) {
                handleRotations();
            } else {
                RotationManager.isUnlocked = false;
            }

            // Handle autoblock
            if (autoblock.isEnabled() && isHoldingSword()) {
                handleAutoblock();
            } else if (wasBlocking) {
                unblock();
                fakeBlocking = false;
            }

            // Handle attack timing
            long now = System.currentTimeMillis();
            long cpsDelay = 1000 / (long) (minCPS.getValue() + random.nextDouble() * (maxCPS.getValue() - minCPS.getValue()));

            if (now - attackTimer >= cpsDelay) {
                attackTimer = now;

                // Ray cast check
                if (rayCast.isEnabled() && !isLookingAtTarget()) {
                    return;
                }

                // Attack
                if (mode.isMode("Multi")) {
                    for (LivingEntity entity : targets) {
                        attackEntity(entity);
                    }
                } else {
                    attackEntity(target);
                }
            }

            // Keep sprint
            if (keepSprint.isEnabled()) {
                mc.player.setSprinting(true);
            }

            // Movement fix
            if (movementFix.isEnabled()) {
                mc.player.setYaw(yaw);
            }
        } else {
            target = null;
            HasTarget = false;
            RotationManager.isUnlocked = false;
            fakeBlocking = false;
            if (wasBlocking) {
                unblock();
            }
        }
    }

    @Override
    public void onRender() {
        if (mc.player != null && HasTarget) {
            RotationManager.setRotation(RTYAW, RTPITCH, RTYAW);
            RotationManager.isUnlocked = true;
        }
        else if (!HasTarget) {
            RotationManager.isUnlocked = false;
        }
    }

    private void sortTargets() {
        targets.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                if (mc.player.squaredDistanceTo(entity) <= reach.getValue() * reach.getValue()
                    && isValidTarget(living)
                    && entity != mc.player) {
                    targets.add(living);
                }
            }
        }

        // Sort based on mode
        switch (sortMode.getMode()) {
            case "Range":
                targets.sort(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)));
                break;
            case "Hurt Time":
                targets.sort(Comparator.comparingLong(e -> e.getLastAttackedTime()));
                break;
            case "Health":
                targets.sort(Comparator.comparingDouble(LivingEntity::getHealth));
                break;
            case "Armor":
                targets.sort(Comparator.comparingInt(e -> e.getArmor()));
                break;
        }
    }

    private boolean isValidTarget(LivingEntity entity) {
        // Players
        if (entity instanceof PlayerEntity) {
            if (targetPlayers.isEnabled()) {
                if (entity.isInvisible()) {
                    return targetInvisibles.isEnabled();
                }
                if (!mc.player.canSee(entity)) {
                    return throughWalls.isEnabled();
                }
                return true;
            }
            return false;
        }

        // Animals
        if (entity instanceof PassiveEntity) {
            return targetAnimals.isEnabled();
        }

        // Mobs
        if (entity instanceof MobEntity) {
            return targetMobs.isEnabled();
        }

        // Invisibles
        if (entity.isInvisible()) {
            return targetInvisibles.isEnabled();
        }

        return false;
    }

    private void handleRotations() {
        if (target == null) return;

        float[] neededRotations = getRotationsNeeded(target);

        if (rotationMode.isMode("Client")) {
            mc.player.setYaw(neededRotations[0]);
            mc.player.setPitch(neededRotations[1]);
            yaw = neededRotations[0];
            pitch = neededRotations[1];
        } else if (rotationMode.isMode("Packet")) {
            RTYAW = neededRotations[0];
            RTPITCH = neededRotations[1];
            yaw = neededRotations[0];
            pitch = neededRotations[1];
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(neededRotations[0], neededRotations[1], mc.player.isOnGround()));
        } else if (rotationMode.isMode("Smooth")) {
            float speed = (float) rotationSpeed.getValue();
            float currentYaw = mc.player.getYaw();
            float currentPitch = mc.player.getPitch();

            float yawDiff = normalizeAngle(neededRotations[0] - currentYaw);
            float pitchDiff = neededRotations[1] - currentPitch;

            float newYaw = currentYaw + yawDiff * (speed / 10.0f);
            float newPitch = currentPitch + pitchDiff * (speed / 10.0f);

            RTYAW = newYaw;
            RTPITCH = newPitch;
            yaw = newYaw;
            pitch = newPitch;
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(newYaw, newPitch, mc.player.isOnGround()));
        }
    }

    private float[] getRotationsNeeded(LivingEntity entity) {
        double dx = entity.getX() - mc.player.getX();
        double dy = entity.getY() + entity.getHeight() / 2.0 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double dz = entity.getZ() - mc.player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        yaw = normalizeAngle(yaw);
        pitch = clamp(pitch, -90, 90);

        return new float[]{yaw, pitch};
    }

    private float normalizeAngle(float angle) {
        angle = angle % 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }
        if (angle < -180.0F) {
            angle += 360.0F;
        }
        return angle;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private boolean isLookingAtTarget() {
        if (target == null) return false;

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d lookVec = mc.player.getRotationVector().multiply(reach.getValue());
        Vec3d endPos = eyePos.add(lookVec);

        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);

        // Simple distance check for ray cast
        return targetPos.squaredDistanceTo(eyePos) <= reach.getValue() * reach.getValue();
    }

    private void attackEntity(LivingEntity entity) {
        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean isHoldingSword() {
        ItemStack held = mc.player.getMainHandStack();
        return held.getItem() instanceof SwordItem;
    }

    private void handleAutoblock() {
        if (!autoblock.isEnabled()) return;

        switch (autoblockMode.getMode()) {
            case "Watchdog":
                if (!wasBlocking) {
                    block();
                    wasBlocking = true;
                }
                // Watchdog releases every 4 ticks
                if (mc.player.age % 4 == 0 && wasBlocking) {
                    unblock();
                    wasBlocking = false;
                }
                break;
            case "Verus":
                if (!wasBlocking) {
                    block();
                    wasBlocking = true;
                }
                break;
            case "Fake":
                // Fake mode - just visual blocking for Animations module
                Animations.animate = true;
                break;
        }
    }

    private void block() {
        if (mc.options.useKey.isPressed()) {
            mc.player.setSprinting(false);
        }
        mc.options.useKey.setPressed(true);
    }

    private void unblock() {
        mc.options.useKey.setPressed(false);
    }
}
