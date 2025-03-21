package com.kijinseija.seija_printer.settings.widgets.input;

import com.kijinseija.seija_printer.mixin.WTextBoxAccessor;
import com.kijinseija.seija_printer.settings.PrinterSettings;
import com.kijinseija.seija_printer.settings.obj.DoubleRange;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.Text;

import java.util.Locale;

public class WDoubleRangeEdit extends WHorizontalList {
    private double valueMin, valueMax;

    private final double min, max;
    private final double sliderMin, sliderMax;

    public int decimalPlaces;
    public boolean noSlider = false;
    public boolean small = false;

    public Runnable action;
    public Runnable actionOnRelease;

    private WTextBox textBoxMin;
    private WTextBox textBoxMax;
    private WRangeSlider slider;

    public WDoubleRangeEdit(double valueMin, double valueMax, double min, double max, double sliderMin, double sliderMax, int decimalPlaces, boolean noSlider) {
        this.valueMin = Math.min(valueMin, valueMax);
        this.valueMax = Math.max(valueMin, valueMax);
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;

        if (noSlider || (sliderMin == 0 && sliderMax == 0)) this.noSlider = true;
    }

    @Override
    public void init() {
        if (!(theme instanceof MeteorGuiTheme)) return;
        //onlySupportMeteor

        textBoxMin = add(theme.textBox(valueString(valueMin), this::filterMin)).minWidth(50).widget();
        if (noSlider) {
            add(theme.button("+")).widget().action = () -> setButtonMin(get()[0] + 1);
            add(theme.button("-")).widget().action = () -> setButtonMin(get()[0] - 1);
        } else
            slider = add(PrinterSettings.getINSTANCE().rangeSlider(valueMin, valueMax, sliderMin, sliderMax)).minWidth(small ? 200 - 100 - spacing : 175).centerY().expandX().widget();

        textBoxMax = add(theme.textBox(valueString(valueMax), this::filterMax)).minWidth(50).widget();
        if (noSlider) {
            add(theme.button("+")).widget().action = () -> setButtonMax(get()[1] + 1);
            add(theme.button("-")).widget().action = () -> setButtonMax(get()[1] - 1);
        }

        textBoxMin.actionOnUnfocused = () -> {
            double lastValue = valueMin;

            if (textBoxMin.get().isEmpty()) valueMin = 0;
            else if (textBoxMin.get().equals("-")) valueMin = -0;
            else if (textBoxMin.get().equals(".")) valueMin = 0;
            else if (textBoxMin.get().equals("-.")) valueMin = 0;
            else {
                try {
                    valueMin = Double.parseDouble(textBoxMin.get());
                } catch (NumberFormatException ignored) {
                }
            }

            double preValidationValue = valueMin;

//            if (valueMin < min) valueMin = min;
//            else if (valueMin > max) valueMin = max;
            valueMin = Math.clamp(valueMin, min, valueMax);
            if (valueMin != preValidationValue) textBoxMin.set(valueString(valueMin));
            if (slider != null) slider.setMin(valueMin);

            if (valueMin != lastValue) {
                if (action != null) action.run();
                if (actionOnRelease != null) actionOnRelease.run();
            }
        };

        textBoxMax.actionOnUnfocused = () -> {
            double lastValue = valueMax;

            if (textBoxMax.get().isEmpty()) valueMax = valueMin;
            else if (textBoxMax.get().equals("-")) valueMax = valueMin;
            else if (textBoxMax.get().equals(".")) valueMax = valueMin;
            else if (textBoxMax.get().equals("-.")) valueMax = valueMin;
            else {
                try {
                    valueMax = Double.parseDouble(textBoxMax.get());
                } catch (NumberFormatException ignored) {
                }
            }

            double preValidationValue = valueMax;

            valueMax = Math.clamp(valueMax, valueMin, max);

            if (valueMax != preValidationValue) textBoxMax.set(valueString(valueMax));
            if (slider != null) slider.setMax(valueMax);

            if (valueMax != lastValue) {
                if (action != null) action.run();
                if (actionOnRelease != null) actionOnRelease.run();
            }
        };

        if (slider != null) {
            slider.action = () -> {
                double lastValueMin = valueMin;
                double lastValueMax = valueMax;

                double[] values = slider.get();
                //使用数据框内超过slider范围的数据时避免拉动slider使超出的数据在不应改变时改变
                if (!((valueMin < sliderMin) && (values[0] == sliderMin)))//实际最小/大值超范围 且拉条的处在最小/大位置时拒绝修改
                    valueMin = values[0];
                if (!((valueMax > sliderMax) && (values[1] == sliderMax)))
                    valueMax = values[1];
                textBoxMin.set(valueString(valueMin));
                textBoxMax.set(valueString(valueMax));

                if (action != null && (valueMin != lastValueMin || valueMax != lastValueMax)) action.run();
            };

            slider.actionOnRelease = () -> {
                if (actionOnRelease != null) actionOnRelease.run();
            };
        }
    }

    private boolean filterMin(String text, char c) {
        return filter(text, c, textBoxMin);
    }

    private boolean filterMax(String text, char c) {
        return filter(text, c, textBoxMax);
    }

    private boolean filter(String text, char c, WTextBox textBox) {
        boolean good;
        boolean validate = true;

        if (c == '-' && !text.contains("-") && ((WTextBoxAccessor) textBox).getCursor() == 0) {
            good = true;
            validate = false;
        } else if (c == '.' && !text.contains(".")) {
            good = true;
            if (text.isEmpty()) validate = false;
        } else good = Character.isDigit(c);

        if (good && validate) {
            try {
                Double.parseDouble(text + c);
            } catch (NumberFormatException ignored) {
                good = false;
            }
        }

        return good;
    }

    private void setButtonMin(double v) {
        if (this.valueMin == v) return;

        if (v < min) this.valueMin = min;
        else this.valueMin = Math.min(v, valueMax);

        if (this.valueMin == v) {
            textBoxMin.set(valueString(this.valueMin));
            if (slider != null) slider.set(this.valueMin, this.valueMax);

            if (action != null) action.run();
            if (actionOnRelease != null) actionOnRelease.run();
        }
    }

    private void setButtonMax(double v) {
        if (this.valueMax == v) return;

        if (v < valueMin) this.valueMax = valueMin;
        else this.valueMax = Math.min(v, max);

        if (this.valueMax == v) {
            textBoxMax.set(valueString(this.valueMax));
            if (slider != null) slider.set(this.valueMin, this.valueMax);

            if (action != null) action.run();
            if (actionOnRelease != null) actionOnRelease.run();
        }
    }

    public double[] get() {
        return new double[]{valueMin, valueMax};
    }

    public DoubleRange getDoubleRange() {
        return new DoubleRange(valueMin, valueMax);
    }

    public void set(DoubleRange range) {
        set(range.value1, range.value2);
    }

    public void set(double value1, double value2) {
        this.valueMin = Math.min(value1, value2);
        this.valueMax = Math.max(value1, value2);

        textBoxMin.set(valueString(this.valueMin));
        textBoxMax.set(valueString(this.valueMax));
        if (slider != null) slider.set(valueMin, valueMax);
    }

    private String valueString(double value) {
        return String.format(Locale.US, "%." + decimalPlaces + "f", value);
    }
}
