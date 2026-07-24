package com.kijinseija.seija_printer.settings.widgets;

import com.kijinseija.seija_printer.settings.core.Setting;
import com.kijinseija.seija_printer.settings.core.Settings;

import java.util.Collection;
import java.util.Map;

/** Text model used by the native settings screen. */
public final class WSelectedCountLabel {
    private final Setting<?> setting;

    public WSelectedCountLabel(Setting<?> setting) {
        this.setting = setting;
    }

    public int size() {
        return getSize(setting);
    }

    public String text() {
        int size = size();
        if (setting.get() instanceof Settings) return "(" + size + " groups)";
        return "(" + size + " selected)";
    }

    public static int getSize(Setting<?> setting) {
        if (setting == null) return 0;
        Object value = setting.get();
        if (value instanceof Collection<?> collection) return collection.size();
        if (value instanceof Map<?, ?> map) return map.size();
        if (value instanceof Settings settings) return settings.groups.size();
        return 0;
    }
}
