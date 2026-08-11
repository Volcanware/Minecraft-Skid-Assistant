package volcanware.anarchyclef.mixins;

import volcanware.anarchyclef.altomenu.modules.Render.RainbowTotem;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TotemParticle.class)
public abstract class TotemParticleMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        TotemParticle self = (TotemParticle) (Object) this;
        Random random = Random.create();

        // Random color between 0.0 and 1.0 for each channel
        float red = random.nextFloat();
        float green = random.nextFloat();
        float blue = random.nextFloat();

        if (RainbowTotem.INSTANCE.isEnabled()) self.setColor(red, green, blue);
    }
}