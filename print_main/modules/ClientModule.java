/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.events.render.Render3DEvent;
import com.kijinseija.seija_printer.settings.core.Settings;
import net.minecraft.client.Minecraft;

/**
 * Small client-side module lifecycle used by Seija Printer.
 *
 * <p>This small registry-facing lifecycle keeps module state on the client
 * thread without requiring an external module host.</p>
 */
public abstract class ClientModule {
    protected static final Minecraft mc = Minecraft.getInstance();

    public final String name;
    public final String description;
    public final Settings settings = new Settings();

    private boolean active;

    protected ClientModule(String name, String description) {
        this.name = name == null ? "" : name;
        this.description = description == null ? "" : description;
    }

    /** Compatibility constructor for modules that still pass a category. */
    protected ClientModule(String ignoredCategory, String name, String description) {
        this(name, description);
    }

    public final boolean isActive() {
        return active;
    }

    public final void toggle() {
        setActive(!active);
    }

    public final void activate() {
        setActive(true);
    }

    public final void deactivate() {
        setActive(false);
    }

    public final void setActive(boolean active) {
        if (this.active == active) return;

        this.active = active;
        if (active) {
            onActivate();
            settings.onModuleActivated();
        } else {
            onDeactivate();
        }
    }

    /** Called once when the module is enabled. */
    public void onActivate() {
    }

    /** Called once when the module is disabled. */
    public void onDeactivate() {
    }

    /** Called from {@code ClientTickEvents.END_CLIENT_TICK} while enabled. */
    public void onClientTick() {
    }

    /** Called from the Fabric level render callback while enabled. */
    public void onRender3d(Render3DEvent event) {
    }

    /** Called when the play connection is closed or the client stops. */
    public void onDisconnect() {
        deactivate();
    }

    /** Compatibility hook for modules that used to announce a toggle. */
    public void sendToggledMsg() {
    }
}
