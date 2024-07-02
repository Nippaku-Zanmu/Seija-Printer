package com.kijinseija.seija_printer.print_main.printer.extra_setting;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.FixerManager;
import meteordevelopment.meteorclient.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class ExtraSettingManager {
    public final static ExtraSettingManager INSTANCE = new ExtraSettingManager();
    private final Settings extraSettings = new Settings();


    public Settings getExtraSettings() {
        return extraSettings;
    }
    public ExtraSettingManager() {
        registerExtraSetting();
        for (HasExtraSetting entry : hasExtraSettings) {
            entry.getSettingGroup(extraSettings);
        }
    }
    private final List<HasExtraSetting> hasExtraSettings = new ArrayList<>();
    private void registerExtraSetting(){
        hasExtraSettings.add(FixerManager.INSTANCE);
    }
}
