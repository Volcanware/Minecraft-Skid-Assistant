// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import net.optifine.CustomColors;
import net.minecraft.src.Config;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.passive.EntityWolf;

public class LayerWolfCollar implements LayerRenderer<EntityWolf>
{
    private static final ResourceLocation WOLF_COLLAR;
    private final RenderWolf wolfRenderer;
    
    static {
        WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
    }
    
    public LayerWolfCollar(final RenderWolf wolfRendererIn) {
        this.wolfRenderer = wolfRendererIn;
    }
    
    @Override
    public void doRenderLayer(final EntityWolf entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.wolfRenderer.bindTexture(LayerWolfCollar.WOLF_COLLAR);
            final EnumDyeColor enumdyecolor = EnumDyeColor.byMetadata(entitylivingbaseIn.getCollarColor().getMetadata());
            float[] afloat = EntitySheep.func_175513_a(enumdyecolor);
            if (Config.isCustomColors()) {
                afloat = CustomColors.getWolfCollarColors(enumdyecolor, afloat);
            }
            GL11.glColor3f(afloat[0], afloat[1], afloat[2]);
            this.wolfRenderer.getMainModel().render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
