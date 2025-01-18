package com.kijinseija.seija_printer.settings;

import com.kijinseija.seija_printer.settings.impl.*;
import com.kijinseija.seija_printer.settings.widgets.input.WDoubleRangeEdit;
import com.kijinseija.seija_printer.settings.obj.DoubleRange;
import com.kijinseija.seija_printer.settings.widgets.input.WMeteorRangeSlider;
import com.kijinseija.seija_printer.settings.widgets.input.WRangeSlider;
import com.kijinseija.seija_printer.settings.widgets.WSelectedCountLabel;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;

import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PrinterSettings {
    private final Map<Class<?>, SettingsWidgetFactory.Factory> factories;

    public static PrinterSettings getINSTANCE() {
        return INSTANCE;
    }

    private static PrinterSettings INSTANCE;

    private final GuiTheme theme;

    public PrinterSettings(Map<Class<?>, SettingsWidgetFactory.Factory> factories, GuiTheme theme) {
        this.factories = factories;
        this.theme = theme;
        INSTANCE = this;
    }

    public void addSettings() {
        //factories.put(StringMapSetting.class, (table, setting) -> stringMapW(table, (StringMapSetting) setting));
        factories.put(DirectionListSetting.class, (table, setting) -> dirSettingW(table, (DirectionListSetting) setting));
        factories.put(SettingsSetting.class, (table, setting) -> settingsW(table, (SettingsSetting) setting));
        factories.put(DoubleRangeSetting.class, (table, setting) -> doubleRangeW(table, (DoubleRangeSetting) setting));
    }


    private void dirSettingW(WTable table, DirectionListSetting setting) {
        WButton button = table.add(theme.button("Select")).expandCellX().widget();
        button.action = () -> mc.setScreen(new DirectionListSettingScreen(theme, setting));

        WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = setting::reset;
    }

    private void settingsW(WTable table, SettingsSetting setting) {
        WHorizontalList widget = table.add(theme.horizontalList()).expandCellX().widget();

        WButton button = widget.add(theme.button("Edit")).expandCellX().widget();
        widget.add(new WSelectedCountLabel(setting).color(theme.textSecondaryColor()));
        button.action = () -> mc.setScreen(new SettingsSettingScreen(theme, setting));
        WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = setting::reset;
    }

    private void doubleRangeW(WTable table, DoubleRangeSetting setting) {
        DoubleRange doubleRange = setting.get();
        WDoubleRangeEdit edit = new WDoubleRangeEdit(doubleRange.value1, doubleRange.value2, setting.min, setting.max, setting.sliderMin, setting.sliderMax, setting.decimalPlaces, setting.noSlider);
        edit.theme = this.theme;
        table.add(edit).expandX();

        Runnable action = () -> {
            if (!setting.set(edit.getDoubleRange())) edit.set(setting.get());
        };

        if (setting.onSliderRelease) edit.actionOnRelease = action;
        else edit.action = action;

        reset(table, setting, () -> edit.set(setting.get()));
    }

    private void reset(WContainer c, Setting<?> setting, Runnable action) {
        WButton reset = c.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = () -> {
            setting.reset();
            if (action != null) action.run();
        };
    }

    public WRangeSlider rangeSlider(double v1, double v2, double min, double max) {
        return w(new WMeteorRangeSlider(v1, v2, min, max));
    }

    protected <T extends WWidget> T w(T widget) {
        widget.theme = this.theme;
        return widget;
    }


}
