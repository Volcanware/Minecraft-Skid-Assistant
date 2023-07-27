package dev.client.tenacity.module;

import dev.client.tenacity.module.impl.render.ArraylistMod;
import dev.client.tenacity.module.impl.render.NotificationsMod;
import dev.client.tenacity.module.impl.render.ScoreboardMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleCollection {

    public static boolean reloadModules;

    private HashMap<Class<? extends Module>, Module> modules = new HashMap<>();
    private final List<Class<? extends Module>> hiddenModules = new ArrayList<>(Arrays.asList(ArraylistMod.class, NotificationsMod.class));

    public List<Class<? extends Module>> getHiddenModules() {
        return hiddenModules;
    }

    public List<Module> getModules() {
        return new ArrayList<>(this.modules.values());
    }

    public void setModules(HashMap<Class<? extends Module>, Module> modules) {
        this.modules = modules;
    }

    public List<Module> getModulesInCategory(Category c) {
        return this.modules.values().stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }

    public Module get(Class<? extends Module> mod) {
        return this.modules.get(mod);
    }

    public Module getModuleByName(String name) {
        return this.modules.values().stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesContains(String text) {
        return this.modules.values().stream().filter(m -> m.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    public final List<Module> getToggledModules() {
        return this.modules.values().stream().filter(Module::isToggled).collect(Collectors.toList());
    }

}
