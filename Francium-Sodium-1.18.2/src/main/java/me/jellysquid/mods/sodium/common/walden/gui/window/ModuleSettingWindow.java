package me.jellysquid.mods.sodium.common.walden.gui.window;

import me.jellysquid.mods.sodium.common.walden.gui.ClickGui;
import me.jellysquid.mods.sodium.common.walden.gui.component.Component;
import me.jellysquid.mods.sodium.common.walden.gui.component.ModuleButtonComponent;
import me.jellysquid.mods.sodium.common.walden.module.Module;
import me.jellysquid.mods.sodium.common.walden.module.setting.Setting;

public class ModuleSettingWindow extends Window {

    private final Module module;
    private final ModuleButtonComponent moduleButton;

    public ModuleSettingWindow(ClickGui parent, double x, double y, Module module, ModuleButtonComponent moduleButton) {
        super(parent, x, y, 150, 200);
        super.closable = true;
        super.minimizable = false;
        super.setTitle(module.getName());
        this.module = module;
        this.moduleButton = moduleButton;
        y = 40;
        for (Setting<?> setting : module.getSettings()) {
            Component component = setting.makeComponent(this);
            if (component != null) {
                component.setX(20);
                component.setY(y);
                addComponent(component);
                y += component.getLength() + 17.0;
            }
        }
    }

    @Override
    public void onClose() {
        moduleButton.settingWindowClosed();
    }

}
