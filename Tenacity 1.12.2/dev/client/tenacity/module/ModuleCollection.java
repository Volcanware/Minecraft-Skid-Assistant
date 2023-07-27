package dev.client.tenacity.module;

import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.module.impl.render.Ambience;
import dev.client.tenacity.module.impl.render.Animations;
import dev.client.tenacity.module.impl.render.ArraylistMod;
import dev.client.tenacity.module.impl.render.BlurModule;
import dev.client.tenacity.module.impl.render.Brightness;
import dev.client.tenacity.module.impl.render.ChinaHat;
import dev.client.tenacity.module.impl.render.ClickGuiMod;
import dev.client.tenacity.module.impl.render.ESP2D;
import dev.client.tenacity.module.impl.render.GlowESP;
import dev.client.tenacity.module.impl.render.HudMod;
import dev.client.tenacity.module.impl.render.NotificationsMod;
import dev.client.tenacity.module.impl.render.Radar;
import dev.client.tenacity.module.impl.render.ScoreboardMod;
import dev.client.tenacity.module.impl.render.SessionStats;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleCollection {
    public static boolean reloadModules;
    private HashMap<Class<? extends Module>, Module> modules = new HashMap();
    private final List<Class<? extends Module>> hiddenModules = new ArrayList<>(Arrays.asList(ArraylistMod.class, NotificationsMod.class));

    public List<Class<? extends Module>> getHiddenModules() {
        return hiddenModules;
    }

    public List<Module> getModules() {
        return new ArrayList<Module>(this.modules.values());
    }

    public void setModules(HashMap<Class<? extends Module>, Module> modules) {
        this.modules = modules;
    }

    public void init() {
        this.modules.put(Ambience.class, new Ambience());
        this.modules.put(Animations.class, new Animations());
        this.modules.put(ArraylistMod.class, new ArraylistMod());
        this.modules.put(BlurModule.class, new BlurModule());
        this.modules.put(Brightness.class, new Brightness());
        this.modules.put(ChinaHat.class, new ChinaHat());
        this.modules.put(ClickGuiMod.class, new ClickGuiMod());
        this.modules.put(ESP2D.class, new ESP2D());
        this.modules.put(GlowESP.class, new GlowESP());
        this.modules.put(HudMod.class, new HudMod());
        this.modules.put(NotificationsMod.class, new NotificationsMod());
        this.modules.put(Radar.class, new Radar());
        this.modules.put(ScoreboardMod.class, new ScoreboardMod());
        this.modules.put(SessionStats.class, new SessionStats());
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