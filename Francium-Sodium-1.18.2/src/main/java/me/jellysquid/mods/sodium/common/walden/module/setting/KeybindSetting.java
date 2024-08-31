package me.jellysquid.mods.sodium.common.walden.module.setting;

import me.jellysquid.mods.sodium.common.walden.ConfigManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import me.jellysquid.mods.sodium.common.walden.gui.component.ButtonComponent;
import me.jellysquid.mods.sodium.common.walden.gui.component.Component;
import me.jellysquid.mods.sodium.common.walden.gui.window.Window;
import me.jellysquid.mods.sodium.common.walden.keybind.Keybind;
import me.jellysquid.mods.sodium.common.walden.module.Module;

import static me.jellysquid.mods.sodium.common.walden.ConfigManager.MC;
import static org.lwjgl.glfw.GLFW.*;

public class KeybindSetting extends Setting<Keybind>
{

	private Keybind value;

	private KeybindSetting(Builder builder)
	{
		super(builder.name, builder.description, builder.module);
		value = builder.value;
	}

	@Override
	public Keybind get()
	{
		return value;
	}

	@Override
	public void set(Keybind value)
	{
		this.value = value;
	}

    private String getKeyName(int keyCode) {
        String keyName = String.valueOf(glfwGetKeyName(keyCode, 0)).toUpperCase();

        switch (keyCode) {
            case GLFW_KEY_ESCAPE: return "ESC";
            case GLFW_KEY_GRAVE_ACCENT: return "GA";
            case GLFW_KEY_WORLD_1: return "W1";
            case GLFW_KEY_WORLD_2: return "W2";
            case GLFW_KEY_PRINT_SCREEN: return "PS";
            case GLFW_KEY_PAUSE: return "PAUSE";
            case GLFW_KEY_INSERT: return "INS";
            case GLFW_KEY_DELETE: return "DEL";
            case GLFW_KEY_HOME: return "HM";
            case GLFW_KEY_PAGE_UP: return "PU";
            case GLFW_KEY_PAGE_DOWN: return "PD";
            case GLFW_KEY_END: return "END";
            case GLFW_KEY_TAB: return "TAB";
            case GLFW_KEY_LEFT_CONTROL: return "LCTRL";
            case GLFW_KEY_RIGHT_CONTROL: return "RCTRL";
            case GLFW_KEY_LEFT_ALT: return "LALT";
            case GLFW_KEY_RIGHT_ALT: return "RALT";
            case GLFW_KEY_LEFT_SHIFT: return "LSHIFT";
            case GLFW_KEY_RIGHT_SHIFT: return "RSHIFT";
            case GLFW_KEY_UP: return "UP";
            case GLFW_KEY_DOWN: return "DOWN";
            case GLFW_KEY_LEFT: return "LEFT";
            case GLFW_KEY_RIGHT: return "RIGHT";
            case GLFW_KEY_BACKSPACE: return "BSPACE";
            case GLFW_KEY_CAPS_LOCK: return "CAPS";
            case GLFW_KEY_MENU: return "MENU";
            case GLFW_KEY_LEFT_SUPER: return "LEFTS";
            case GLFW_KEY_RIGHT_SUPER: return "RIGHTS";
            case GLFW_KEY_ENTER: return "ENTER";
            case GLFW_KEY_KP_ENTER: return "NENTER";
            case GLFW_KEY_NUM_LOCK: return "NLOCK";
            case GLFW_KEY_SPACE: return "SPACE";
            case GLFW_KEY_F1: return "F1";
            case GLFW_KEY_F2: return "F2";
            case GLFW_KEY_F3: return "F3";
            case GLFW_KEY_F4: return "F4";
            case GLFW_KEY_F5: return "F5";
            case GLFW_KEY_F6: return "F6";
            case GLFW_KEY_F7: return "F7";
            case GLFW_KEY_F8: return "F8";
            case GLFW_KEY_F9: return "F9";
            case GLFW_KEY_F10: return "F10";
            case GLFW_KEY_F11: return "F11";
            case GLFW_KEY_F12: return "F12";
            default:
                if (keyName != "NULL")
                    return keyName;
        }

        return "UNK";
    }

	@Override
	public Component makeComponent(Window parent)
	{
		return new ButtonComponent(parent, 0, 0, 40, getName(), () ->
				MC.setScreen(new Screen(new LiteralText(""))
				{

					private final Screen prev = MC.currentScreen;

					@Override
					public boolean keyPressed(int keyCode, int scanCode, int modifiers)
					{
						value.setKey(keyCode);
						MC.setScreen(prev);
						ConfigManager.INSTANCE.getKeybindManager().removeKeybind(ConfigManager.INSTANCE.getKeybindManager().getAllKeybinds().get(0));
						ConfigManager.INSTANCE.getKeybindManager().addDefaultKeybinds();
						return false;
					}

					@Override
					public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
					{
						renderBackground(matrices);
						drawCenteredText(matrices, MC.textRenderer, "Please input your key...", width / 2, height / 2, 0xFFFFFF);
					}
				}), () -> String.valueOf(getKeyName(value.getKey())));
	}

	public static class Builder
	{
		private String name;
		private String description;
		private Module module;
		private Keybind value;

		public static Builder newInstance()
		{
			return new Builder();
		}

		public KeybindSetting build()
		{
			return new KeybindSetting(this);
		}

		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}

		public Builder setDescription(String description)
		{
			this.description = description;
			return this;
		}

		public Builder setModule(Module module)
		{
			this.module = module;
			return this;
		}

		public Builder setValue(Keybind value)
		{
			this.value = value;
			return this;
		}
	}
}
