/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorLabel;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;

import java.util.Collection;
import java.util.Map;

public  class WSelectedCountLabel extends WMeteorLabel {
        private final Setting<?> setting;
        private int lastSize = -1;

        public WSelectedCountLabel(Setting<?> setting) {
            super("", false);

            this.setting = setting;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            int size = getSize(setting);

            if (size != lastSize) {
                if (setting.get()instanceof Settings){
                    set("(" + size + " Group)");
                }else
                    set("(" + size + " selected)");
                lastSize = size;
            }

            super.onRender(renderer, mouseX, mouseY, delta);
        }

        public static int getSize(Setting<?> setting) {
            if (setting.get() instanceof Collection<?> collection) return collection.size();
            if (setting.get() instanceof Map<?, ?> map) return map.size();
            if (setting.get() instanceof Settings) return ((Settings) setting.get()).groups.size();
            return -1;
        }
    }
