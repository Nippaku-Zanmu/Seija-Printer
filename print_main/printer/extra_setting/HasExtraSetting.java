/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.extra_setting;

import com.kijinseija.seija_printer.settings.core.SettingGroup;
import com.kijinseija.seija_printer.settings.core.Settings;

public interface HasExtraSetting {
    SettingGroup getSettingGroup(Settings settings);
}
