// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.monster.EntityEnderman;

public class ModelAdapterEnderman extends ModelAdapterBiped
{
    public ModelAdapterEnderman() {
        super(EntityEnderman.class, "enderman", 0.5f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelEnderman(0.0f);
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderEnderman renderenderman = new RenderEnderman(rendermanager);
        renderenderman.mainModel = modelBase;
        renderenderman.shadowSize = shadowSize;
        return renderenderman;
    }
}
