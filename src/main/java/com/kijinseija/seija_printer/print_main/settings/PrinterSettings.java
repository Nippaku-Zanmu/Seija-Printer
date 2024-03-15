package com.kijinseija.seija_printer.print_main.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

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
        factories.put(DirectionListSetting.class, (table, setting) -> gameModeListW(table, (DirectionListSetting) setting));
    }

//    private void stringMapW(WTable table, StringMapSetting setting) {
//        WTable wtable = table.add(theme.table()).expandX().widget();
//        StringMapSetting.fillTable(theme, wtable, setting);
//    }

    private void gameModeListW(WTable table, DirectionListSetting setting) {
        WButton button = table.add(theme.button("Select")).expandCellX().widget();
        button.action = () -> mc.setScreen(new DirectionListSettingScreen(theme, setting));

        WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = setting::reset;
    }
}
