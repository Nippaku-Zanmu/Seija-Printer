/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.gui;

import com.kijinseija.seija_printer.print_main.InitClass;
import com.kijinseija.seija_printer.print_main.modules.ClientModule;
import com.kijinseija.seija_printer.print_main.modules.ItemSearcher;
import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.task_manager.RotationManager;
import com.kijinseija.seija_printer.print_main.printer.util.RenderUtil;
import com.kijinseija.seija_printer.settings.PrinterSettings;
import com.kijinseija.seija_printer.settings.core.BlockPosSetting;
import com.kijinseija.seija_printer.settings.core.BlockListSetting;
import com.kijinseija.seija_printer.settings.core.BoolSetting;
import com.kijinseija.seija_printer.settings.core.ColorSetting;
import com.kijinseija.seija_printer.settings.core.DoubleSetting;
import com.kijinseija.seija_printer.settings.core.EnumSetting;
import com.kijinseija.seija_printer.settings.core.IntSetting;
import com.kijinseija.seija_printer.settings.core.Setting;
import com.kijinseija.seija_printer.settings.core.SettingColor;
import com.kijinseija.seija_printer.settings.core.SettingGroup;
import com.kijinseija.seija_printer.settings.core.StringSetting;
import com.kijinseija.seija_printer.settings.obj.DoubleRange;
import com.kijinseija.seija_printer.settings.impl.DoubleRangeSetting;
import com.kijinseija.seija_printer.settings.impl.DirectionListSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Standalone settings and runtime diagnostics screen.
 *
 * <p>The screen deliberately uses only vanilla client widgets.  It can be
 * opened while in a world ({@link #isPauseScreen()} returns {@code false}),
 * so module ticks and rendering continue while values are being inspected.</p>
 */
public final class PrinterDebugScreen extends Screen {
    private static final int ROW_HEIGHT = 28;
    private static final int GROUP_HEIGHT = 22;
    private static final int PANEL_MARGIN = 12;
    private static final int BOTTOM_BAR_HEIGHT = 36;

    private final Screen parent;
    private final InitClass runtime;
    private final List<SettingRow> rows = new ArrayList<>();

    private int selectedModule;
    private int scrollOffset;
    private int contentHeight;
    private String status = "";

    public PrinterDebugScreen(Screen parent, InitClass runtime) {
        super(Component.translatable("screen.seija_printer.debug.title"));
        this.parent = parent;
        this.runtime = runtime;
        this.selectedModule = 0;
    }

    /** Convenience entry point for key mappings and other client integrations. */
    public static void open(Minecraft client, InitClass runtime) {
        if (client != null) client.setScreen(new PrinterDebugScreen(client.screen, runtime));
    }

    @Override
    protected void init() {
        clearWidgets();
        rows.clear();

        List<ClientModule> modules = modules();
        if (selectedModule >= modules.size()) selectedModule = Math.max(0, modules.size() - 1);

        int panelLeft = panelLeft();
        int panelRight = panelRight();
        int panelTop = panelTop();
        int panelBottom = panelBottom();

        addModuleTabs(modules, panelLeft, 28);

        ClientModule module = currentModule();
        int actionY = 54;
        int actionWidth = Math.min(112, Math.max(84, (panelRight - panelLeft - 18) / 5));
        addRenderableWidget(Button.builder(module == null
                ? Component.translatable("screen.seija_printer.debug.no_module")
                : Component.translatable(module.isActive()
                    ? "screen.seija_printer.debug.disable"
                    : "screen.seija_printer.debug.enable"), button -> {
            if (module != null) {
                module.toggle();
                status = module.name + (module.isActive() ? " enabled" : " disabled");
                rebuildWidgets();
            }
        }).bounds(panelLeft, actionY, actionWidth, 20).build());

        if (module instanceof ItemSearcher searcher) {
            int searchActionY = 78;
            int searchActionWidth = (panelRight - panelLeft - 6) / 2;
            addRenderableWidget(Button.builder(Component.translatable("screen.seija_printer.debug.start_analysis"), button -> {
                if (!searcher.isActive()) searcher.activate();
                searcher.startAnalysis();
                status = "Analysis started";
            }).bounds(panelLeft, searchActionY, searchActionWidth, 20).build());
            addRenderableWidget(Button.builder(Component.translatable("screen.seija_printer.debug.print_list"), button -> {
                searcher.printItemList();
                status = "Material list sent to chat";
            }).bounds(panelLeft + searchActionWidth + 6, searchActionY, searchActionWidth, 20).build());
        }

        int saveX = panelRight - 2 * 76 - 6;
        addRenderableWidget(Button.builder(Component.translatable("screen.seija_printer.debug.save"), button -> {
            saveSettings();
            status = "Settings saved";
        }).bounds(saveX, actionY, 76, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.seija_printer.debug.reset_all"), button -> {
            if (module != null) {
                module.settings.reset();
                status = "Settings reset";
                rebuildWidgets();
            }
        }).bounds(panelRight - 76, actionY, 76, 20).build());

        if (module != null) buildSettingRows(module, panelLeft, panelRight, panelTop, panelBottom);
        if (contentHeight <= panelBottom - panelTop) scrollOffset = 0;
        else scrollOffset = Math.min(scrollOffset, maxScroll(panelTop, panelBottom));
    }

    private void addModuleTabs(List<ClientModule> modules, int left, int y) {
        int x = left;
        int available = Math.max(100, panelRight() - left);
        int width = Math.max(96, Math.min(170, available / Math.max(1, modules.size())));
        for (int i = 0; i < modules.size(); i++) {
            ClientModule module = modules.get(i);
            final int index = i;
            Button tab = Button.builder(Component.literal(module.name), button -> {
                selectedModule = index;
                scrollOffset = 0;
                status = "";
                rebuildWidgets();
            }).bounds(x, y, width - 4, 20).build();
            if (!module.description.isBlank()) {
                tab.setTooltip(Tooltip.create(Component.literal(module.description)));
            }
            tab.active = selectedModule != i;
            addRenderableWidget(tab);
            x += width;
        }
    }

    private void buildSettingRows(ClientModule module, int left, int right, int top, int bottom) {
        int y = top + 6 - scrollOffset;
        int labelWidth = Math.max(90, Math.min(260, (right - left) * 40 / 100));
        int controlX = left + labelWidth;
        int resetX = right - 56;
        int controlWidth = Math.max(72, resetX - controlX - 8);

        for (SettingGroup group : module.settings.values()) {
            if (group.settings.stream().noneMatch(Setting::isVisible)) continue;
            rows.add(SettingRow.group(group.name, y));
            y += GROUP_HEIGHT;

            for (Setting<?> setting : group.settings) {
                if (!setting.isVisible()) continue;
                int rowY = y;
                rows.add(SettingRow.setting(setting, rowY));
                if (isWithinPanel(rowY, top, bottom)) {
                    addSettingWidget(setting, controlX, rowY, controlWidth);
                    addRenderableWidget(Button.builder(Component.translatable("screen.seija_printer.debug.reset"), button -> {
                        setting.reset();
                        status = setting.name + " reset";
                        rebuildWidgets();
                    }).bounds(resetX, rowY - 1, 54, 20).build());
                }
                y += ROW_HEIGHT;
            }
        }
        contentHeight = Math.max(0, y + scrollOffset - top);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addSettingWidget(Setting<?> setting, int x, int y, int width) {
        Component label = Component.literal(setting.name);
        String description = setting.description;
        Tooltip tooltip = description == null || description.isBlank()
            ? null : Tooltip.create(Component.literal(description));

        if (setting instanceof BoolSetting boolSetting) {
            Checkbox.Builder builder = Checkbox.builder(Component.empty(), font)
                .pos(x, y)
                .selected(Boolean.TRUE.equals(boolSetting.get()))
                .onValueChange((checkbox, value) -> {
                    boolSetting.set(value);
                    minecraft.execute(() -> {
                        if (minecraft.screen == this) rebuildWidgets();
                    });
                });
            if (tooltip != null) builder.tooltip(tooltip);
            addRenderableWidget(builder.build());
            return;
        }

        if (setting instanceof EnumSetting<?> enumSetting) {
            Enum<?> current = (Enum<?>) enumSetting.get();
            if (current != null) {
                List<Enum<?>> values = Arrays.asList(current.getDeclaringClass().getEnumConstants());
                CycleButton.Builder<Enum<?>> builder = CycleButton.<Enum<?>>builder(
                    value -> Component.literal(value.name()),
                    () -> (Enum<?>) enumSetting.get()
                ).withValues(values);
                CycleButton<Enum<?>> cycle = builder.create(x, y - 1, width, 20, label,
                    (button, value) -> ((Setting) enumSetting).set(value));
                if (tooltip != null) cycle.setTooltip(tooltip);
                addRenderableWidget(cycle);
                return;
            }
        }

        if (setting instanceof IntSetting || setting instanceof DoubleSetting
            || setting instanceof StringSetting || setting instanceof BlockPosSetting
            || setting instanceof DoubleRangeSetting || setting instanceof ColorSetting
            || setting instanceof BlockListSetting || setting instanceof DirectionListSetting) {
            EditBox box = new EditBox(font, x, y - 1, width, 20, label);
            box.setValue(formatValue(setting.get()));
            box.setMaxLength(256);
            box.setResponder(setting::parse);
            if (tooltip != null) box.setTooltip(tooltip);
            addRenderableWidget(box);
            return;
        }

        // Complex settings (block lists, colors, nested settings) remain
        // intentionally read-only here; the reset button still works.
        if (tooltip != null) {
            // The tooltip is attached to a compact button so read-only values
            // are still discoverable without introducing a custom widget.
            Button info = Button.builder(Component.literal(trimValue(formatValue(setting.get()), width)), button -> {
                status = setting.name + ": " + formatValue(setting.get());
            }).bounds(x, y - 1, width, 20).tooltip(tooltip).build();
            addRenderableWidget(info);
        } else {
            addRenderableWidget(Button.builder(Component.literal(trimValue(formatValue(setting.get()), width)), button -> {
                status = setting.name + ": " + formatValue(setting.get());
            }).bounds(x, y - 1, width, 20).build());
        }
    }

    private static String formatValue(Object value) {
        if (value == null) return "<unset>";
        if (value instanceof BlockPos pos) return pos.getX() + " " + pos.getY() + " " + pos.getZ();
        if (value instanceof DoubleRange range) return range.value1 + " " + range.value2;
        if (value instanceof SettingColor color) {
            return color.r + "," + color.g + "," + color.b + "," + color.a + "," + color.rainbow;
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(entry -> {
                if (entry instanceof Block block) return BuiltInRegistries.BLOCK.getKey(block).toString();
                if (entry instanceof Direction direction) return direction.getName();
                return String.valueOf(entry);
            }).reduce((left, right) -> left + "," + right).orElse("");
        }
        if (value instanceof Enum<?> enumValue) return enumValue.name();
        return String.valueOf(value);
    }

    private static String trimValue(String value, int width) {
        int max = Math.max(12, width / 7);
        if (value.length() <= max) return value;
        return value.substring(0, Math.max(1, max - 3)) + "...";
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int left = panelLeft();
        int right = panelRight();
        int top = panelTop();
        int bottom = panelBottom();

        graphics.fill(left, top, right, bottom, 0x990B0F14);
        graphics.outline(left, top, right, bottom, 0xFF59616B);
        graphics.centeredText(font, title, width / 2, 8, 0xFFFFFFFF);

        ClientModule module = currentModule();
        if (!status.isBlank()) graphics.text(font, Component.literal(status), left + 8, bottom + 9, 0xFFE0C46C);
        graphics.text(font, Component.literal(diagnostics()), left + 8, bottom + 21, 0xFF9FAAB5);

        int labelWidth = Math.max(90, Math.min(260, (right - left) * 40 / 100));
        for (SettingRow row : rows) {
            if (row.group()) {
                if (isWithinPanel(row.y(), top, bottom)) {
                    graphics.text(font, Component.literal(row.groupName()), left + 8, row.y() + 5, 0xFF76B9FF);
                    graphics.horizontalLine(left + 8, right - 8, row.y() + 18, 0x664A6176);
                }
            } else if (isWithinPanel(row.y(), top, bottom)) {
                String label = font.plainSubstrByWidth(row.setting().name, Math.max(20, labelWidth - 12), true);
                graphics.text(font, Component.literal(label), left + 8, row.y() + 5, 0xFFE8E8E8);
            }
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseY >= panelTop() && mouseY <= panelBottom() && contentHeight > panelBottom() - panelTop()) {
            int step = verticalAmount > 0 ? -ROW_HEIGHT : ROW_HEIGHT;
            int next = Math.max(0, Math.min(maxScroll(panelTop(), panelBottom()), scrollOffset + step));
            if (next != scrollOffset) {
                scrollOffset = next;
                rebuildWidgets();
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void onClose() {
        saveSettings();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void saveSettings() {
        if (runtime != null) PrinterSettings.getINSTANCE().saveModules(runtime.modules());
        else PrinterSettings.getINSTANCE().save(Printer.getINSTANCE().settings);
    }

    private String diagnostics() {
        String world = minecraft.level == null ? "world: none" : "world: loaded";
        String player = minecraft.player == null ? "player: none" : "player: " + minecraft.player.getGameProfile().name();
        String queue = "rotation: " + RotationManager.INSTANCE.taskSize();
        String render = "render: " + RenderUtil.renderList.size();
        ClientModule module = currentModule();
        String moduleState = module == null ? "module: none"
            : "module: " + (module.isActive() ? "active" : "inactive");
        if (module instanceof ItemSearcher searcher) {
            return trimText(moduleState + " | " + world + " | " + player + " | " + queue + " | " + render
                + " | materials: " + searcher.materialCounts().size(), panelRight() - panelLeft() - 16);
        }
        return trimText(moduleState + " | " + world + " | " + player + " | " + queue + " | " + render,
            panelRight() - panelLeft() - 16);
    }

    private String trimText(String text, int pixelWidth) {
        int maxChars = Math.max(20, pixelWidth / 6);
        if (text.length() <= maxChars) return text;
        return text.substring(0, Math.max(1, maxChars - 3)) + "...";
    }

    private List<ClientModule> modules() {
        if (runtime != null) return runtime.modules();
        return List.of(Printer.getINSTANCE());
    }

    private ClientModule currentModule() {
        List<ClientModule> modules = modules();
        return modules.isEmpty() ? null : modules.get(Math.max(0, Math.min(selectedModule, modules.size() - 1)));
    }

    private int panelLeft() {
        int panelWidth = Math.min(640, Math.max(260, width - PANEL_MARGIN * 2));
        return (width - panelWidth) / 2;
    }

    private int panelRight() {
        int panelWidth = Math.min(640, Math.max(260, width - PANEL_MARGIN * 2));
        return (width + panelWidth) / 2;
    }

    private int panelTop() {
        return currentModule() instanceof ItemSearcher ? 106 : 82;
    }

    private int panelBottom() {
        return Math.max(panelTop() + 40, height - BOTTOM_BAR_HEIGHT);
    }

    private int maxScroll(int top, int bottom) {
        return Math.max(0, contentHeight - (bottom - top));
    }

    private static boolean isWithinPanel(int y, int top, int bottom) {
        return y + ROW_HEIGHT >= top && y <= bottom - 2;
    }

    private record SettingRow(String groupName, Setting<?> setting, int y, boolean group) {
        static SettingRow group(String name, int y) {
            return new SettingRow(name, null, y, true);
        }

        static SettingRow setting(Setting<?> setting, int y) {
            return new SettingRow("", setting, y, false);
        }
    }
}
