// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntityCreeper;

public class ModelAdapterCreeper extends ModelAdapter
{
    public ModelAdapterCreeper() {
        super(EntityCreeper.class, "creeper", 0.5f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelCreeper();
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelCreeper)) {
            return null;
        }
        final ModelCreeper modelcreeper = (ModelCreeper)model;
        return modelPart.equals("head") ? modelcreeper.head : (modelPart.equals("armor") ? modelcreeper.creeperArmor : (modelPart.equals("body") ? modelcreeper.body : (modelPart.equals("leg1") ? modelcreeper.leg1 : (modelPart.equals("leg2") ? modelcreeper.leg2 : (modelPart.equals("leg3") ? modelcreeper.leg3 : (modelPart.equals("leg4") ? modelcreeper.leg4 : null))))));
    }
    
    @Override
    public String[] getModelRendererNames() {
        return new String[] { "head", "armor", "body", "leg1", "leg2", "leg3", "leg4" };
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderCreeper rendercreeper = new RenderCreeper(rendermanager);
        rendercreeper.mainModel = modelBase;
        rendercreeper.shadowSize = shadowSize;
        return rendercreeper;
    }
}
