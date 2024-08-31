package com.alan.clients.module.impl.movement.longjump;

import com.alan.clients.component.impl.player.PacketlessDamageComponent;
import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;

import com.alan.clients.module.impl.movement.LongJump;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;

/**
 * @author Auth
 * @since 18/11/2021
 */

public class FireBallLongJump extends Mode<LongJump> {

    private NumberValue height = new NumberValue("Height", this, 1, 0.42, 9, 0.1);
    private NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 3, 0.1);
    private int tick;
    private double moveSpeed = 0;
    private float yawAtDamage;

    public FireBallLongJump(String name, LongJump parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {


        if (mc.thePlayer.hurtTime == 9) {
            yawAtDamage = mc.thePlayer.rotationYaw;
            mc.thePlayer.motionY = height.getValue().floatValue() - (height.getValue().floatValue() > 0.45 ? Math.random() / 1000f : 0);
            moveSpeed = speed.getValue().floatValue() - Math.random() / 1000f;
        }

        if (mc.thePlayer.ticksSinceVelocity <= 10) {
            MoveUtil.strafe(moveSpeed, yawAtDamage);
            moveSpeed -= (moveSpeed / 269.9) + Math.random() / 100f;
        }

        int item = SlotUtil.findItem(Items.fire_charge);

        if (mc.thePlayer.onGroundTicks == 1) {
            MoveUtil.stop();
        }

        if (item == -1) return;

        tick++;

        SlotComponent.setSlot(item);

        if (tick == 2) {
            PacketUtil.send(new C08PacketPlayerBlockPlacement(SlotComponent.getItemStack()));
        }

        RotationComponent.setRotations(new Vector2f(mc.thePlayer.rotationYaw, 90), 10, MovementFix.OFF);
    };

    @Override
    public void onEnable() {
        tick = 0;
    }
}