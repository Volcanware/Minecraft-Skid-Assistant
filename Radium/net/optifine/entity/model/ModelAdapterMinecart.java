// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityMinecart;

public class ModelAdapterMinecart extends ModelAdapter
{
    public ModelAdapterMinecart() {
        super(EntityMinecart.class, "minecart", 0.5f);
    }
    
    protected ModelAdapterMinecart(final Class entityClass, final String name, final float shadow) {
        super(entityClass, name, shadow);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelMinecart();
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelMinecart)) {
            return null;
        }
        final ModelMinecart modelminecart = (ModelMinecart)model;
        return modelPart.equals("bottom") ? modelminecart.sideModels[0] : (modelPart.equals("back") ? modelminecart.sideModels[1] : (modelPart.equals("front") ? modelminecart.sideModels[2] : (modelPart.equals("right") ? modelminecart.sideModels[3] : (modelPart.equals("left") ? modelminecart.sideModels[4] : (modelPart.equals("dirt") ? modelminecart.sideModels[5] : null)))));
    }
    
    @Override
    public String[] getModelRendererNames() {
        return new String[] { "bottom", "back", "front", "right", "left", "dirt" };
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderMinecart renderminecart = new RenderMinecart(rendermanager);
        if (!Reflector.RenderMinecart_modelMinecart.exists()) {
            Config.warn("Field not found: RenderMinecart.modelMinecart");
            return null;
        }
        Reflector.setFieldValue(renderminecart, Reflector.RenderMinecart_modelMinecart, modelBase);
        renderminecart.shadowSize = shadowSize;
        return renderminecart;
    }
}
