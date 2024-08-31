// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.entity.model;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

public abstract class ModelAdapterQuadruped extends ModelAdapter
{
    public ModelAdapterQuadruped(final Class entityClass, final String name, final float shadowSize) {
        super(entityClass, name, shadowSize);
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelQuadruped)) {
            return null;
        }
        final ModelQuadruped modelquadruped = (ModelQuadruped)model;
        return modelPart.equals("head") ? modelquadruped.head : (modelPart.equals("body") ? modelquadruped.body : (modelPart.equals("leg1") ? modelquadruped.leg1 : (modelPart.equals("leg2") ? modelquadruped.leg2 : (modelPart.equals("leg3") ? modelquadruped.leg3 : (modelPart.equals("leg4") ? modelquadruped.leg4 : null)))));
    }
    
    @Override
    public String[] getModelRendererNames() {
        return new String[] { "head", "body", "leg1", "leg2", "leg3", "leg4" };
    }
}
