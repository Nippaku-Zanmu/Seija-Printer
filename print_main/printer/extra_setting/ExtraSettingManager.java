/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.extra_setting;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.FixerManager;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.PlaceDataManager;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide.MainDecide;
import com.kijinseija.seija_printer.settings.core.Settings;
import java.util.ArrayList;
import java.util.List;

public class ExtraSettingManager {
    private static ExtraSettingManager INSTANCE = new ExtraSettingManager();


    public synchronized static ExtraSettingManager getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ExtraSettingManager();
        }
        return INSTANCE;
    }

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

    private void registerExtraSetting() {
        hasExtraSettings.add(FixerManager.INSTANCE);
        hasExtraSettings.add(PlaceDataManager.getInstance());
        hasExtraSettings.add(MainDecide.INSTANCE);
    }
}
