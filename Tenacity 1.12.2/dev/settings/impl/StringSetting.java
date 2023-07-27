package dev.settings.impl;

import dev.settings.Setting;

public class StringSetting
extends Setting {
    private String string = "";

    public StringSetting(String name) {
        this.name = name;
    }

    public StringSetting(String name, String defaultValue) {
        this.name = name;
        this.string = defaultValue;
    }

    public String getString() {
        return this.string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getConfigValue() {
        return this.string;
    }
}