/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */



package com.kijinseija.seija_printer.settings.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;

public class WMeteorRangeSlider extends WRangeSlider implements MeteorWidget {
    public WMeteorRangeSlider(double value1, double value2, double min, double max) {
        super(value1,value2, min, max);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double valueWidth1 = valueWidth(value1);
        double valueWidth2 = valueWidth(value2);

        renderBar(renderer, valueWidth1,valueWidth2);
        renderHandle(renderer, valueWidth1,valueWidth2);
    }

    private void renderBar(GuiRenderer renderer, double valueWidth1,double valueWidth2) {
        MeteorGuiTheme theme = theme();

        double s = theme.scale(3);
        double handleSize = handleSize();

        double x = this.x + handleSize / 2;
        double y = this.y + height / 2 - s / 2;
        double vMin = Math.min(valueWidth1,valueWidth2);
        double vMax = Math.max(valueWidth1,valueWidth2);

        renderer.quad(x, y, vMin, s, theme.sliderRight.get());
        renderer.quad(x + vMin, y, vMax-vMin, s, theme.sliderLeft.get());
        renderer.quad(x + vMax, y, width - vMax - handleSize, s, theme.sliderRight.get());

    }

    private void renderHandle(GuiRenderer renderer, double valueWidth1,double valueWidth2) {
        MeteorGuiTheme theme = theme();
        double s = handleSize();

        renderer.quad(x + valueWidth1, y, s, s, GuiRenderer.CIRCLE, theme.sliderHandle.get(dragging, handleMouseOverH1));
        renderer.quad(x + valueWidth2, y, s, s, GuiRenderer.CIRCLE, theme.sliderHandle.get(dragging, handleMouseOverH2));

    }
}
