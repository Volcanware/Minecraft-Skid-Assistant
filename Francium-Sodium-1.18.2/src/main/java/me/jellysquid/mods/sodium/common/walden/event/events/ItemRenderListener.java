package me.jellysquid.mods.sodium.common.walden.event.events;

import me.jellysquid.mods.sodium.common.walden.event.CancellableEvent;
import me.jellysquid.mods.sodium.common.walden.event.Listener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.ArrayList;

public interface ItemRenderListener extends Listener {

    void onItemRender(ItemRenderEvent event);

    class ItemRenderEvent extends CancellableEvent<ItemRenderListener> {
        private final AbstractClientPlayerEntity player;
        private final float tickDelta;
        private final float pitch;
        private final Hand hand;
        private final float swingProgress;
        private final ItemStack item;
        private final float equipProgress;
        private final MatrixStack matrices;
        private final VertexConsumerProvider vertexConsumers;
        private final int light;

        public ItemRenderEvent(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            this.player = player;
            this.tickDelta = tickDelta;
            this.pitch = pitch;
            this.hand = hand;
            this.swingProgress = swingProgress;
            this.item = item;
            this.equipProgress = equipProgress;
            this.matrices = matrices;
            this.vertexConsumers = vertexConsumers;
            this.light = light;
        }

        public AbstractClientPlayerEntity getPlayer()
        {
            return player;
        }

        public float getTickDelta() {
            return tickDelta;
        }

        public float getPitch() {
            return pitch;
        }

        public Hand getHand() {
            return hand;
        }

        public float getSwingProgress() {
            return swingProgress;
        }

        public ItemStack getItem() {
            return item;
        }

        public float getEquipProgress() {
            return equipProgress;
        }

        public MatrixStack getMatrices() {
            return matrices;
        }

        public VertexConsumerProvider getVertexConsumers() {
            return vertexConsumers;
        }

        public int getLight() {
            return light;
        }

        @Override
        public void fire(ArrayList<ItemRenderListener> listeners) {
            for (ItemRenderListener listener : listeners) {
                listener.onItemRender(this);
            }
        }

        @Override
        public Class<ItemRenderListener> getListenerType()
        {
            return ItemRenderListener.class;
        }
    }

}
