/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.print_main.modules.*;
import com.kijinseija.seija_printer.settings.PrinterSettings;
import meteordevelopment.meteorclient.systems.modules.Modules;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.logging.Logger;

public class InitClass {
    public InitClass() {

        PrinterSettings.getINSTANCE().addSettings();


        // debug only

//        Modules.get().add(new PlaceDebug());
//        Modules.get().add(new ScheDebug());


        Modules.get().add(Printer.getINSTANCE());
        Modules.get().add(new ItemSearcher());
    }
}
