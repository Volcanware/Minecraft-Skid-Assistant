// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntitySnowman;

public class ModelAdapterSnowman extends ModelAdapter
{
    public ModelAdapterSnowman() {
        super(EntitySnowman.class, "snow_golem", 0.5f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelSnowMan();
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelSnowMan)) {
            return null;
        }
        final ModelSnowMan modelsnowman = (ModelSnowMan)model;
        return modelPart.equals("body") ? modelsnowman.body : (modelPart.equals("body_bottom") ? modelsnowman.bottomBody : (modelPart.equals("head") ? modelsnowman.head : (modelPart.equals("left_hand") ? modelsnowman.leftHand : (modelPart.equals("right_hand") ? modelsnowman.rightHand : null))));
    }
    
    @Override
    public String[] getModelRendererNames() {
        return new String[] { "body", "body_bottom", "head", "right_hand", "left_hand" };
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderSnowMan rendersnowman = new RenderSnowMan(rendermanager);
        rendersnowman.mainModel = modelBase;
        rendersnowman.shadowSize = shadowSize;
        return rendersnowman;
    }
}
