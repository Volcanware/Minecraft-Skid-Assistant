// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.entity.model;

import java.util.Set;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.src.Config;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomModelRegistry
{
    private static Map<String, ModelAdapter> mapModelAdapters;
    
    static {
        CustomModelRegistry.mapModelAdapters = makeMapModelAdapters();
    }
    
    private static Map<String, ModelAdapter> makeMapModelAdapters() {
        final Map<String, ModelAdapter> map = new LinkedHashMap<String, ModelAdapter>();
        addModelAdapter(map, new ModelAdapterArmorStand());
        addModelAdapter(map, new ModelAdapterBat());
        addModelAdapter(map, new ModelAdapterBlaze());
        addModelAdapter(map, new ModelAdapterBoat());
        addModelAdapter(map, new ModelAdapterCaveSpider());
        addModelAdapter(map, new ModelAdapterChicken());
        addModelAdapter(map, new ModelAdapterCow());
        addModelAdapter(map, new ModelAdapterCreeper());
        addModelAdapter(map, new ModelAdapterDragon());
        addModelAdapter(map, new ModelAdapterEnderCrystal());
        addModelAdapter(map, new ModelAdapterEnderman());
        addModelAdapter(map, new ModelAdapterEndermite());
        addModelAdapter(map, new ModelAdapterGhast());
        addModelAdapter(map, new ModelAdapterGuardian());
        addModelAdapter(map, new ModelAdapterHorse());
        addModelAdapter(map, new ModelAdapterIronGolem());
        addModelAdapter(map, new ModelAdapterLeadKnot());
        addModelAdapter(map, new ModelAdapterMagmaCube());
        addModelAdapter(map, new ModelAdapterMinecart());
        addModelAdapter(map, new ModelAdapterMinecartTnt());
        addModelAdapter(map, new ModelAdapterMinecartMobSpawner());
        addModelAdapter(map, new ModelAdapterMooshroom());
        addModelAdapter(map, new ModelAdapterOcelot());
        addModelAdapter(map, new ModelAdapterPig());
        addModelAdapter(map, new ModelAdapterPigZombie());
        addModelAdapter(map, new ModelAdapterRabbit());
        addModelAdapter(map, new ModelAdapterSheep());
        addModelAdapter(map, new ModelAdapterSilverfish());
        addModelAdapter(map, new ModelAdapterSkeleton());
        addModelAdapter(map, new ModelAdapterSlime());
        addModelAdapter(map, new ModelAdapterSnowman());
        addModelAdapter(map, new ModelAdapterSpider());
        addModelAdapter(map, new ModelAdapterSquid());
        addModelAdapter(map, new ModelAdapterVillager());
        addModelAdapter(map, new ModelAdapterWitch());
        addModelAdapter(map, new ModelAdapterWither());
        addModelAdapter(map, new ModelAdapterWitherSkull());
        addModelAdapter(map, new ModelAdapterWolf());
        addModelAdapter(map, new ModelAdapterZombie());
        addModelAdapter(map, new ModelAdapterSheepWool());
        addModelAdapter(map, new ModelAdapterBanner());
        addModelAdapter(map, new ModelAdapterBook());
        addModelAdapter(map, new ModelAdapterChest());
        addModelAdapter(map, new ModelAdapterChestLarge());
        addModelAdapter(map, new ModelAdapterEnderChest());
        addModelAdapter(map, new ModelAdapterHeadHumanoid());
        addModelAdapter(map, new ModelAdapterHeadSkeleton());
        addModelAdapter(map, new ModelAdapterSign());
        return map;
    }
    
    private static void addModelAdapter(final Map<String, ModelAdapter> map, final ModelAdapter modelAdapter) {
        addModelAdapter(map, modelAdapter, modelAdapter.getName());
        final String[] astring = modelAdapter.getAliases();
        if (astring != null) {
            for (int i = 0; i < astring.length; ++i) {
                final String s = astring[i];
                addModelAdapter(map, modelAdapter, s);
            }
        }
        final ModelBase modelbase = modelAdapter.makeModel();
        final String[] astring2 = modelAdapter.getModelRendererNames();
        for (int j = 0; j < astring2.length; ++j) {
            final String s2 = astring2[j];
            final ModelRenderer modelrenderer = modelAdapter.getModelRenderer(modelbase, s2);
            if (modelrenderer == null) {
                Config.warn("Model renderer not found, model: " + modelAdapter.getName() + ", name: " + s2);
            }
        }
    }
    
    private static void addModelAdapter(final Map<String, ModelAdapter> map, final ModelAdapter modelAdapter, final String name) {
        if (map.containsKey(name)) {
            Config.warn("Model adapter already registered for id: " + name + ", class: " + modelAdapter.getEntityClass().getName());
        }
        map.put(name, modelAdapter);
    }
    
    public static ModelAdapter getModelAdapter(final String name) {
        return CustomModelRegistry.mapModelAdapters.get(name);
    }
    
    public static String[] getModelNames() {
        final Set<String> set = CustomModelRegistry.mapModelAdapters.keySet();
        final String[] astring = set.toArray(new String[set.size()]);
        return astring;
    }
}
