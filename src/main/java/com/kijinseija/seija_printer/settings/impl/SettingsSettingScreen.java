/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.impl;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;

import static meteordevelopment.meteorclient.utils.Utils.getWindowWidth;

public class SettingsSettingScreen extends WindowScreen {
    private final SettingsSetting setting;

    private WContainer settingsContainer;
    public SettingsSettingScreen(GuiTheme theme,SettingsSetting setting){
        super(theme, setting.name);
        this.setting = setting;

    }
    @Override
    public void initWidgets() {
         //Description
        add(theme.label(setting.description, getWindowWidth() / 2.0));
        // Settings
        if (!setting.get().groups.isEmpty()) {
            settingsContainer = add(theme.verticalList()).expandX().widget();
            settingsContainer.add(theme.settings(setting.get())).expandX();
        }

        // Custom widget
//        WWidget widget = module.getWidget(theme);
//
//        if (widget != null) {
//            add(theme.horizontalSeparator()).expandX();
//            Cell<WWidget> cell = add(widget);
//            if (widget instanceof WContainer) cell.expandX();
//        }
//
        // Bottom
        WHorizontalList bottom = add(theme.horizontalList()).expandX().widget();


    }
    @Override
    public void tick() {
        super.tick();

        setting.get().tick(settingsContainer, theme);
    }

//    @Override
//    public boolean toClipboard() {
//        return NbtUtils.toClipboard(module.title, module.toTag());
//    }
//
//    @Override
//    public boolean fromClipboard() {
//        NbtCompound clipboard = NbtUtils.fromClipboard(module.toTag());
//
//        if (clipboard != null) {
//            module.fromTag(clipboard);
//            return true;
//        }
//
//        return false;
//    }

}
