package me.jellysquid.mods.sodium.mixin.features.texture_updates;

import me.jellysquid.mods.sodium.client.util.color.ColorMixer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sprite.Interpolation.class)
public class MixinSpriteInterpolated {
    @Shadow
    @Final
    private NativeImage[] images;

    @Unique
    private Sprite parent;

    private static final int STRIDE = 4;

    /**
     * @author IMS
     * @reason Replace fragile Shadow
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    public void assignParent(Sprite parent, Sprite.Info info, int maxLevel, CallbackInfo ci) {
        this.parent = parent;
    }

    /**
     * @author JellySquid
     * @reason Drastic optimizations
     */
    @Overwrite
    void apply(Sprite.Animation animation) {
        Sprite.AnimationFrame animationFrame = animation.frames.get(animation.frameIndex);

        int curIndex = animationFrame.index;
        int nextIndex = animation.frames.get((animation.frameIndex + 1) % animation.frames.size()).index;

        if (curIndex == nextIndex) {
            return;
        }

        float delta = 1.0F - (float) animation.frameTicks / (float) animationFrame.time;

        int f1 = ColorMixer.getStartRatio(delta);
        int f2 = ColorMixer.getEndRatio(delta);

        for (int layer = 0; layer < this.images.length; layer++) {
            int width = this.parent.width >> layer;
            int height = this.parent.height >> layer;

            int curX = ((curIndex % animation.frameCount) * width);
            int curY = ((curIndex / animation.frameCount) * height);

            int nextX = ((nextIndex % animation.frameCount) * width);
            int nextY = ((nextIndex / animation.frameCount) * height);

            NativeImage src = this.parent.images[layer];
            NativeImage dst = this.images[layer];

            // Source pointers
            long s1p = src.pointer + (curX + ((long) curY * src.getWidth()) * STRIDE);
            long s2p = src.pointer + (nextX + ((long) nextY * src.getWidth()) * STRIDE);

            // Destination pointers
            long dp = dst.pointer;

            int pixelCount = width * height;

            for (int i = 0; i < pixelCount; i++) {
                MemoryUtil.memPutInt(dp, ColorMixer.mixARGB(MemoryUtil.memGetInt(s1p), MemoryUtil.memGetInt(s2p), f1, f2));

                s1p += STRIDE;
                s2p += STRIDE;
                dp += STRIDE;
            }
        }

        this.parent.upload(0, 0, this.images);
    }

}
