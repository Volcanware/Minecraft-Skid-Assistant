package volcanware.anarchyclef.altomenu.modules.Movement;

import volcanware.anarchyclef.altomenu.Mod;
import volcanware.anarchyclef.altomenu.cheatUtils.CMoveUtil;
import volcanware.anarchyclef.altomenu.settings.BooleanSetting;
import volcanware.anarchyclef.altomenu.settings.ModeSetting;
import volcanware.anarchyclef.altomenu.settings.NumberSetting;
import volcanware.anarchyclef.eventbus.EventHandler;
import volcanware.anarchyclef.mixinManager.RotationManager;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Scaffold module modeled after FDPClient's Scaffold.
 *
 * Replicated FDP feature set:
 *  - Rotation modes: Off / Normal / Stabilized / GodBridge / Backwards
 *  - Rotation smoothing (Speed) + KeepRotation (keep looking at last spot after placing)
 *  - Tower modes: None / Vanilla / Motion / Jump
 *  - Eagle (auto-sneak at block edges) with EagleSneakDelay
 *  - AutoBlock with silent slot swap and SwingMode
 *  - SameY (lock placement height) and Down (build downwards)
 *  - Expand (look-ahead placement distance)
 *  - Zitter (Off / Teleport / Smooth) jitter for speed bypass
 *  - SafeWalk-style edge guard and placement delay
 */
public class ScaffoldFDP extends Mod {

    // --- Rotation ---
    public final ModeSetting rotationMode = new ModeSetting("Rotation", "Stabilized",
            "Off", "Normal", "Stabilized", "GodBridge", "Backwards");
    public final NumberSetting rotationSpeed = new NumberSetting("RotationSpeed", 1, 180, 90, 1);
    public final BooleanSetting keepRotation = new BooleanSetting("KeepRotation", true);
    public final NumberSetting keepTicks = new NumberSetting("KeepTicks", 0, 20, 3, 1);

    // --- Placement ---
    public final NumberSetting expand = new NumberSetting("Expand", 0, 4, 1, 0.5);
    public final NumberSetting placeRange = new NumberSetting("Range", 1.0, 6.0, 4.5, 0.1);
    public final NumberSetting delay = new NumberSetting("Delay", 0, 10, 0, 1);
    public final BooleanSetting sameY = new BooleanSetting("SameY", false);
    public final BooleanSetting down = new BooleanSetting("Down", false);

    // --- AutoBlock / Swing ---
    public final BooleanSetting autoBlock = new BooleanSetting("AutoBlock", true);
    public final BooleanSetting silentSwap = new BooleanSetting("SilentSwap", true);
    public final ModeSetting swingMode = new ModeSetting("Swing", "Client", "Client", "Silent", "None");

    // --- Tower ---
    public final ModeSetting tower = new ModeSetting("Tower", "Vanilla", "None", "Vanilla", "Motion", "Jump");
    public final NumberSetting towerMotion = new NumberSetting("TowerMotion", 0.40, 0.50, 0.42, 0.01);

    // --- Eagle ---
    public final BooleanSetting eagle = new BooleanSetting("Eagle", false);
    public final NumberSetting eagleSneakDelay = new NumberSetting("EagleSneakDelay", 0, 10, 2, 1);

    // --- Zitter (speed jitter) ---
    public final ModeSetting zitterMode = new ModeSetting("Zitter", "Off", "Off", "Teleport", "Smooth");
    public final NumberSetting zitterSpeed = new NumberSetting("ZitterSpeed", 0.1, 1.0, 0.3, 0.1);
    public final NumberSetting zitterStrength = new NumberSetting("ZitterStrength", 0.1, 1.0, 0.5, 0.1);

    // --- Misc ---
    public final BooleanSetting safeWalk = new BooleanSetting("SafeWalk", true);

    // State
    private float lastYaw, lastPitch;
    private boolean isRotating = false;
    private int keepRotationTicks = 0;
    private int delayTimer = 0;
    private int eagleTimer = 0;
    private boolean zitterDirection = false;
    private int placeY = 0;
    private int oldSlot = -1;

    public ScaffoldFDP() {
        super("ScaffoldFDP", "FDPClient-style scaffold with rotations, tower, eagle and zitter.", Category.MOVEMENT);
        addSettings(
                rotationMode, rotationSpeed, keepRotation, keepTicks,
                expand, placeRange, delay, sameY, down,
                autoBlock, silentSwap, swingMode,
                tower, towerMotion,
                eagle, eagleSneakDelay,
                zitterMode, zitterSpeed, zitterStrength,
                safeWalk
        );
    }

    @Override
    public void onEnable() {
        if (isNull()) {
            toggle();
            return;
        }
        placeY = mc.player.getBlockPos().getY() - 1;
        delayTimer = 0;
        keepRotationTicks = 0;
        eagleTimer = 0;
        isRotating = false;
        oldSlot = mc.player.getInventory().selectedSlot;
        lastYaw = mc.player.getYaw();
        lastPitch = mc.player.getPitch();
    }

    @Override
    public void onDisable() {
        isRotating = false;
        keepRotationTicks = 0;
        RotationManager.isUnlocked = false;
        if (mc.options != null) mc.options.sneakKey.setPressed(false);
    }

    @Override
    public void onRender() {
        if (mc.player == null) return;
        if (rotationMode.isMode("Off")) {
            RotationManager.isUnlocked = false;
            return;
        }
        if (isRotating || (keepRotation.isEnabled() && keepRotationTicks > 0)) {
            RotationManager.setRotation(lastYaw, lastPitch, lastYaw);
            RotationManager.isUnlocked = true;
        } else {
            RotationManager.isUnlocked = false;
        }
    }

    @EventHandler
    public boolean onShitTick() {
        if (mc.player == null || mc.world == null) return false;

        isRotating = false;
        if (keepRotationTicks > 0) keepRotationTicks--;
        if (delayTimer > 0) delayTimer--;

        // Lock placement Y when SameY is on and player is on ground
        if (sameY.isEnabled() && mc.player.isOnGround()) {
            placeY = mc.player.getBlockPos().getY() - 1;
        }

        // Tower handling (vertical building while holding jump)
        handleTower();

        // Zitter speed bypass
        handleZitter();

        // Locate a placement target using look-ahead prediction
        BlockPos target = findTarget();
        if (target == null) {
            if (eagle.isEnabled()) mc.options.sneakKey.setPressed(false);
            return false;
        }

        // Edge guard (Eagle / SafeWalk share one sneak decision)
        handleSneak();

        // Find an existing neighbor block to click against
        Direction side = findPlaceableSide(target);
        if (side == null) {
            return false;
        }

        BlockPos neighbor = target.offset(side);
        Direction clickFace = side.getOpposite();
        Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(clickFace.getVector()).multiply(0.5));

        // Range check
        if (mc.player.getEyePos().distanceTo(hitVec) > placeRange.getValue()) {
            return false;
        }

        // Compute rotations toward the placement spot
        if (!rotationMode.isMode("Off")) {
            float[] rots = computeRotations(hitVec);
            float targetYaw = rots[0];
            float targetPitch = rots[1];
            // Smooth toward target
            lastYaw = stepRotation(lastYaw, targetYaw, (float) rotationSpeed.getValue());
            lastPitch = stepRotation(lastPitch, targetPitch, (float) rotationSpeed.getValue());
            isRotating = true;
            mc.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.LookAndOnGround(lastYaw, lastPitch, mc.player.isOnGround()));
        }

        // Respect placement delay
        if (delayTimer > 0) return true;

        // Place
        BlockHitResult hit = new BlockHitResult(hitVec, clickFace, neighbor, false);
        placeBlock(hit);
        delayTimer = (int) delay.getValue();

        if (keepRotation.isEnabled()) keepRotationTicks = (int) keepTicks.getValue();
        return true;
    }

    // ---------- Targeting ----------

    private BlockPos findTarget() {
        Vec3d vel = mc.player.getVelocity();
        double exp = expand.getValue();
        double baseY = sameY.isEnabled() ? placeY : mc.player.getBlockPos().getY() - 1;

        // Build a descending bridge when Down is enabled
        if (down.isEnabled() && !sameY.isEnabled()) {
            baseY -= 1;
        }

        // Scan ahead along the velocity vector (FDP "search" style daisy chaining)
        double maxLook = 1.0 + exp;
        for (double i = 0; i <= maxLook; i += 0.5) {
            Vec3d predict = mc.player.getPos().add(vel.x * (1 + i), 0, vel.z * (1 + i));
            BlockPos pos = BlockPos.ofFloored(predict.x, baseY, predict.z);
            if (mc.world.getBlockState(pos).isAir() && hasNeighbor(pos)) {
                return pos;
            }
        }

        // Fallback: directly below the player
        BlockPos below = BlockPos.ofFloored(mc.player.getX(), baseY, mc.player.getZ());
        if (mc.world.getBlockState(below).isAir() && hasNeighbor(below)) {
            return below;
        }
        return null;
    }

    private boolean hasNeighbor(BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (!mc.world.getBlockState(pos.offset(dir)).isAir()) return true;
        }
        return false;
    }

    private Direction findPlaceableSide(BlockPos pos) {
        // Prefer the downward face for clean scaffolding, then horizontals
        Direction[] order = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};
        for (Direction dir : order) {
            BlockPos neighbor = pos.offset(dir);
            if (!mc.world.getBlockState(neighbor).isAir()) {
                return dir;
            }
        }
        return null;
    }

    // ---------- Rotations ----------

    private float[] computeRotations(Vec3d target) {
        Vec3d eyes = mc.player.getEyePos();
        double dx = target.x - eyes.x;
        double dy = target.y - eyes.y;
        double dz = target.z - eyes.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        switch (rotationMode.getMode()) {
            case "Backwards" -> {
                // Look opposite to movement, straight down-ish (classic NCP bypass)
                yaw = mc.player.getYaw() + 180f;
                pitch = 75f;
            }
            case "GodBridge" -> {
                // Snap yaw to nearest 45-degree diagonal, steep pitch for sprint bridging
                yaw = Math.round(mc.player.getYaw() / 45f) * 45f + 180f;
                pitch = 73f + (float) (Math.random() * 5f);
            }
            case "Stabilized" -> {
                // Keep yaw close to player facing, clamp pitch for legit look
                yaw = mc.player.getYaw();
                pitch = MathHelper.clamp(pitch, 60f, 82f);
            }
            // "Normal" -> use raw computed yaw/pitch
            default -> {
            }
        }
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90f, 90f)};
    }

    private float stepRotation(float current, float target, float speed) {
        float diff = MathHelper.wrapDegrees(target - current);
        diff = MathHelper.clamp(diff, -speed, speed);
        return MathHelper.wrapDegrees(current + diff);
    }

    // ---------- Placement ----------

    private void placeBlock(BlockHitResult hit) {
        int slot = -1;
        oldSlot = mc.player.getInventory().selectedSlot;

        if (autoBlock.isEnabled()) {
            slot = findBlockSlot();
            if (slot == -1) return;
            if (slot != oldSlot) {
                mc.player.getInventory().selectedSlot = slot;
                if (silentSwap.isEnabled()) {
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                }
            }
        } else if (!(mc.player.getMainHandStack().getItem() instanceof BlockItem)) {
            return;
        }

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);

        switch (swingMode.getMode()) {
            case "Client" -> mc.player.swingHand(Hand.MAIN_HAND);
            case "Silent" -> mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            // "None" -> no swing
        }

        // Restore slot after silent swap
        if (autoBlock.isEnabled() && silentSwap.isEnabled() && slot != -1 && slot != oldSlot) {
            mc.player.getInventory().selectedSlot = oldSlot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(oldSlot));
        }
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem && stack.getCount() > 0) {
                return i;
            }
        }
        return -1;
    }

    // ---------- Tower ----------

    private void handleTower() {
        if (tower.isMode("None")) return;
        if (!mc.options.jumpKey.isPressed()) return;
        if (mc.player.isOnGround()) return;

        switch (tower.getMode()) {
            case "Vanilla" -> {
                if (mc.player.getVelocity().y < 0) {
                    mc.player.setVelocity(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
                }
            }
            case "Motion" -> mc.player.setVelocity(mc.player.getVelocity().x, towerMotion.getValue(), mc.player.getVelocity().z);
            case "Jump" -> {
                if (mc.player.getVelocity().y < 0.1) {
                    mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
                }
            }
        }
    }

    // ---------- Eagle ----------

    private void handleSneak() {
        if (!eagle.isEnabled() && !safeWalk.isEnabled()) return;

        // SafeWalk: keep sneaking whenever near an edge so we never fall off
        if (safeWalk.isEnabled() && mc.player.isOnGround()
                && mc.world.getBlockState(mc.player.getSteppingPos()).isAir()) {
            mc.options.sneakKey.setPressed(true);
            return;
        }

        // Eagle: sneak when over the edge (block directly below is air) while moving
        if (eagle.isEnabled() && CMoveUtil.isMoving()) {
            boolean overEdge = mc.world.getBlockState(mc.player.getBlockPos().down()).isAir();
            if (overEdge) {
                eagleTimer = (int) eagleSneakDelay.getValue();
                mc.options.sneakKey.setPressed(true);
                return;
            } else if (eagleTimer > 0) {
                eagleTimer--;
                mc.options.sneakKey.setPressed(true);
                return;
            }
        }

        mc.options.sneakKey.setPressed(false);
    }

    // ---------- Zitter ----------

    private void handleZitter() {
        if (zitterMode.isMode("Off")) return;
        if (!CMoveUtil.isMoving() || !mc.player.isOnGround()) return;

        double strength = zitterStrength.getValue();
        zitterDirection = !zitterDirection;
        float yaw = mc.player.getYaw();

        if (zitterMode.isMode("Teleport")) {
            double side = zitterDirection ? 90 : -90;
            double rad = Math.toRadians(yaw + side);
            double mx = -Math.sin(rad) * strength * zitterSpeed.getValue();
            double mz = Math.cos(rad) * strength * zitterSpeed.getValue();
            mc.player.setVelocity(mc.player.getVelocity().x + mx, mc.player.getVelocity().y, mc.player.getVelocity().z + mz);
        } else { // Smooth
            mc.player.sidewaysSpeed = zitterDirection ? (float) zitterSpeed.getValue() : -(float) zitterSpeed.getValue();
        }
    }
}
