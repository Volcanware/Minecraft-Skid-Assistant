// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntitySkeleton;

public class ModelAdapterSkeleton extends ModelAdapterBiped
{
    public ModelAdapterSkeleton() {
        super(EntitySkeleton.class, "skeleton", 0.7f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelSkeleton();
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderSkeleton renderskeleton = new RenderSkeleton(rendermanager);
        Render.setModelBipedMain(renderskeleton, (ModelBiped)modelBase);
        renderskeleton.mainModel = modelBase;
        renderskeleton.shadowSize = shadowSize;
        return renderskeleton;
    }
}
