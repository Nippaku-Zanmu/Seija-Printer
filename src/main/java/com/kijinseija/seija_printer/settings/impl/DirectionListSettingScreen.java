package com.kijinseija.seija_printer.settings.impl;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.util.math.Direction;

import java.util.List;

public class DirectionListSettingScreen extends WindowScreen {
    private final DirectionListSetting setting;
    private final WTable table;

    public DirectionListSettingScreen(GuiTheme theme, DirectionListSetting setting) {
        super(theme, "Select Gamemodes");
        this.setting = setting;
        table = super.add(theme.table()).expandX().widget();
    }

    @Override
    public void initWidgets() {
        List<Direction> gms = setting.get();
        for (Direction direction : Direction.values()) {
            table.add(theme.label(Utils.nameToTitle(direction.getName()))).expandCellX();

            boolean contains = setting.get().contains(direction);
            WCheckbox checkbox = table.add(theme.checkbox(contains)).widget();
            checkbox.action = () -> {
                if (contains) {
                    gms.remove(direction);
                } else {
                    gms.add(direction);
                }
            };

            table.row();
        }
    }
}
