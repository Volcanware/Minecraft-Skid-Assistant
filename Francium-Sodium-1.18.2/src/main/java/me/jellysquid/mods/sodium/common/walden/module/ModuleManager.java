package me.jellysquid.mods.sodium.common.walden.module;

import me.jellysquid.mods.sodium.common.walden.module.modules.client.*;
import me.jellysquid.mods.sodium.common.walden.module.modules.combat.*;
import me.jellysquid.mods.sodium.common.walden.module.modules.render.*;
import me.jellysquid.mods.sodium.common.walden.module.modules.misc.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class ModuleManager
{
    private final HashMap<Class<? extends Module>, Module> modulesByClass = new HashMap<>();
    private final HashMap<String, Module> modulesByName = new HashMap<>();
    private final HashSet<Module> modules = new HashSet<>();

    public ModuleManager()
    {
        addModules();
    }

    public ArrayList<Module> getModules()
    {
        ArrayList<Module> arrayList = new ArrayList<>(modules);
        arrayList.sort(Comparator.comparing(Module::getName));
        return arrayList;
    }

    public int getSizeOfModulesByCategory(Category category) {
        ArrayList<Module> arrayList = getModules();
        int size = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getCategory() == category)
                size++;
        }
        return size;
    }

    public int getSizeOfModulesByCategory(String categoryName) {
        return getSizeOfModulesByCategory(getCategoryByName(categoryName));
    }

    public Category getCategoryByName(String categoryName) {

        Category[] categories = {Category.CLIENT, Category.COMBAT, Category.RENDER, Category.MISC};

        for (Category category : categories) {
            if (category.toString().equals(categoryName)) {
                return category;
            }
        }

        return null;

    }

    public Module getModule(Class<? extends Module> clazz)
    {
        return modulesByClass.get(clazz);
    }

    public Module getModuleByName(String name)
    {
        return modulesByName.get(name);
    }

    private void addModules()
    {
        addModule(Cringe.class);
        addModule(ADH.class);
        addModule(AR.class);
        addModule(CC.class);
        addModule(ALT.class);
        addModule(AITL.class);
        addModule(TB.class);
        addModule(AIT.class);
        addModule(AD.class);
        addModule(AHC.class);
        addModule(CGS.class);
        addModule(FC.class);
        addModule(MC.class);
        addModule(PH.class);
        addModule(AM.class);
        addModule(AX.class);
        addModule(MCP.class);
        addModule(DC.class);
        addModule(SD.class);
        addModule(NHC.class);
        addModule(AP.class);
        addModule(VW.class);
        addModule(HB.class);
        addModule(AMC.class);
        addModule(CR.class);
        addModule(AWT.class);
        addModule(ATL.class);
    }

    private void addModule(Class<? extends Module> clazz) {
        try {
            Module module = clazz.getConstructor().newInstance();
            modulesByClass.put(clazz, module);
            modulesByName.put(module.getName(), module);
            modules.add(module);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void removeModules() {
        for (Module module : this.getModules()) {
            removeModule(module.getClass());
        }
    }

    private void removeModule(Class<? extends Module> clazz) {
        modulesByClass.remove(clazz);
        modulesByName.remove(clazz);
        modules.remove(clazz);
    }
}