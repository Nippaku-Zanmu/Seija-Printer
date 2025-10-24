package com.kijinseija.seija_printer.settings.impl;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.settings.BlockListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.settings.BlockListSetting;

import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockReplaceSettingScreen extends WindowScreen {


    public BlockReplaceSettingScreen(GuiTheme theme, BlockReplaceSetting setting) {
        super(theme, "BlockReplace");
        this.setting = setting;
    }

    private final BlockReplaceSetting setting;
    WTable table = add(theme.table()).expandX().widget();

    private WTextBox filter;
    private String filterText = "";

    @Override
    public void initWidgets() {
//        table = add(theme.table()).expandX().widget();
        widgets();
    }

    public void widgets() {


//        if (table.rowI()!=setting.get().size()+1)
//            table.clear();

        for (Map.Entry<List<Block>, List<Block>> entry : setting.get().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            List<Block> keyBlocks = entry.getKey();
            WButton editKeys = table.add(theme.button(GuiRenderer.EDIT)).widget();
            int keyRenderCount = 0;//渲染的预览被替换方块数
            for (; keyRenderCount < keyBlocks.size() && keyRenderCount < 4; keyRenderCount++) {
                table.add(theme.item(keyBlocks.get(keyRenderCount).asItem().getDefaultStack()));
            }//方块预览
            int extraKeyBlockCount = keyBlocks.size() - 4;
            table.add(theme.tooltip(extraKeyBlockCount > 0 ? "+" + extraKeyBlockCount + "  ->  " : "  ->  "));
            //额外数量
            editKeys.action = () -> {
                BlockListSetting keySetting = new BlockListSetting.Builder()
                    .name("keyBlock").defaultValue(keyBlocks).build();
                //调用流星的方块选择要转换setting

                BlockListSettingScreen keyScreen = new BlockListSettingScreen(theme, keySetting);
                //创建流星的方块选择窗口
                keyScreen.onClosed(() -> {
                    keyBlocks.clear();
                    keyBlocks.addAll(keySetting.get());
                    table.clear();
                    widgets();
                });//关闭窗口时应用选择的配置
                mc.setScreen(keyScreen);
            };


//            setting.get().computeIfAbsent(keyBlocks, k -> new ArrayList<>());
            List<Block> valueBlocks = entry.getValue();
            WButton editValues = table.add(theme.button(GuiRenderer.EDIT)).widget();

            int valRenderCount = 0;//渲染的预览替换方块数
            for (; valRenderCount < valueBlocks.size() && valRenderCount <= 4; valRenderCount++) {
                table.add(theme.item(valueBlocks.get(valRenderCount).asItem().getDefaultStack()));
            }//方块预览
            int extraValBlockCount = valueBlocks.size() - 4;
            table.add(theme.tooltip(extraValBlockCount > 0 ? "+" + extraValBlockCount : ""));
            //额外数量

            editValues.action = () -> {
                BlockListSetting valueSetting = new BlockListSetting.Builder()
                    .name("valueBlock").defaultValue(valueBlocks).build();
                BlockListSettingScreen valueScreen = new BlockListSettingScreen(theme, valueSetting);
                valueScreen.onClosed(() -> {
                    valueBlocks.clear();
                    valueBlocks.addAll(valueSetting.get());
                    table.clear();
                    widgets();
                });
                mc.setScreen(valueScreen);
            };


            WPressable button = table.add(theme.minus()).expandCellX().right().widget();
            button.action = () -> {
                keyBlocks.clear();
                valueBlocks.clear();
                setting.get().put(keyBlocks, null);
                setting.get().remove(keyBlocks);
                table.clear();
                widgets();
            };
            table.row();
        }
//        for (List<Block> keyBlocks : setting.get().values()) {
//
//
//        }
        WButton add = table.add(theme.button("Add")).expandCellX().center().widget();
        add.action = () -> {
            setting.get().put(new ArrayList<>(), new ArrayList<>());
            table.clear();
            widgets();
        };
    }
}
