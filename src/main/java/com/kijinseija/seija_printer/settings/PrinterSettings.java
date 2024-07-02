package com.kijinseija.seija_printer.settings;

import com.kijinseija.seija_printer.settings.impl.DirectionListSetting;
import com.kijinseija.seija_printer.settings.impl.DirectionListSettingScreen;
import com.kijinseija.seija_printer.settings.impl.SettingsSetting;
import com.kijinseija.seija_printer.settings.impl.SettingsSettingScreen;
import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorLabel;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;

import java.util.Collection;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PrinterSettings {
    private final Map<Class<?>, SettingsWidgetFactory.Factory> factories;

    private final GuiTheme theme;

    public PrinterSettings(Map<Class<?>, SettingsWidgetFactory.Factory> factories, GuiTheme theme) {
        this.factories = factories;
        this.theme = theme;
    }

    public void addSettings() {
        //factories.put(StringMapSetting.class, (table, setting) -> stringMapW(table, (StringMapSetting) setting));
        factories.put(DirectionListSetting.class, (table, setting) -> dirSettingW(table, (DirectionListSetting) setting));
        factories.put(SettingsSetting.class,(table, setting) -> settingsW(table,(SettingsSetting)setting));
    }

//    private void stringMapW(WTable table, StringMapSetting setting) {
//        WTable wtable = table.add(theme.table()).expandX().widget();
//        StringMapSetting.fillTable(theme, wtable, setting);
//    }

    private void dirSettingW(WTable table, DirectionListSetting setting) {
        WButton button = table.add(theme.button("Select")).expandCellX().widget();
        button.action = () -> mc.setScreen(new DirectionListSettingScreen(theme, setting));

        WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = setting::reset;
    }
    private void settingsW(WTable table, SettingsSetting setting){
        WHorizontalList widget = table.add(theme.horizontalList()).expandCellX().widget();

        WButton button = widget.add(theme.button("Edit")).expandCellX().widget();
        widget.add(new WSelectedCountLabel(setting).color(theme.textSecondaryColor()));
        button.action = () -> mc.setScreen(new SettingsSettingScreen(theme, setting));
        WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = setting::reset;
    }



    private static class WSelectedCountLabel extends WMeteorLabel {
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
}
