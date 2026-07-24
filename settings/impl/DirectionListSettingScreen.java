package com.kijinseija.seija_printer.settings.impl;

import net.minecraft.core.Direction;

/** Simple editor used by the native configuration screen. */
public final class DirectionListSettingScreen {
    private final DirectionListSetting setting;

    public DirectionListSettingScreen(DirectionListSetting setting) {
        this.setting = setting;
    }

    /** Compatibility constructor for callers that used to pass a GUI theme. */
    public DirectionListSettingScreen(Object ignoredTheme, DirectionListSetting setting) {
        this(setting);
    }

    public void toggle(Direction direction) {
        if (setting.get().contains(direction)) setting.get().remove(direction);
        else setting.get().add(direction);
    }
}
