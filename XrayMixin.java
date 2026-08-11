package volcanware.anarchyclef.mixins;

import volcanware.anarchyclef.altomenu.modules.Render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class XrayMixin {

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void xray$shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> ci) {
        if (Xray.isActive()) {
            ci.setReturnValue(Xray.instance.isXrayBlock(state.getBlock()));
        }
    }
}
