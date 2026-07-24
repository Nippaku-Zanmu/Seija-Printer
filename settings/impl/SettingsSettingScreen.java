package com.kijinseija.seija_printer.settings.impl;

/** Adapter for editing nested settings from the native configuration screen. */
public final class SettingsSettingScreen {
    private final SettingsSetting setting;

    public SettingsSettingScreen(SettingsSetting setting) {
        this.setting = setting;
    }

    public SettingsSettingScreen(Object ignoredTheme, SettingsSetting setting) {
        this(setting);
    }

    public SettingsSetting setting() {
        return setting;
    }
}
