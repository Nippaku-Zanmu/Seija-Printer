/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main;

import com.kijinseija.seija_printer.events.render.Render3DEvent;
import com.kijinseija.seija_printer.print_main.modules.ClientModule;
import com.kijinseija.seija_printer.print_main.modules.ItemSearcher;
import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.settings.PrinterSettings;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Owns the standalone module registry and dispatches Fabric callbacks. */
public final class InitClass {
    private final List<ClientModule> modules = new CopyOnWriteArrayList<>();

    public InitClass() {
        register(Printer.getINSTANCE());
        register(new ItemSearcher());
        for (ClientModule module : modules) PrinterSettings.getINSTANCE().loadModule(module);
    }

    public void register(ClientModule module) {
        if (module != null && !modules.contains(module)) modules.add(module);
    }

    public List<ClientModule> modules() {
        return List.copyOf(modules);
    }

    public void onClientTick() {
        for (ClientModule module : modules) {
            if (module.isActive()) module.onClientTick();
        }
    }

    public void onRender3d(Render3DEvent event) {
        for (ClientModule module : modules) {
            if (module.isActive()) module.onRender3d(event);
        }
    }

    public void onDisconnect() {
        for (ClientModule module : modules) module.onDisconnect();
    }
}
