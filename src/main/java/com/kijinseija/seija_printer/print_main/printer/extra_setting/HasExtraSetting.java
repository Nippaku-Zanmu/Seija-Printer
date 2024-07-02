package com.kijinseija.seija_printer.print_main.printer.extra_setting;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;

public interface HasExtraSetting {
    SettingGroup getSettingGroup(Settings settings);
}
