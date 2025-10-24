/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package com.kijinseija.seija_printer.settings.widgets.input;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import net.minecraft.util.math.MathHelper;

public abstract class WRangeSlider extends WWidget {
    public Runnable action;
    public Runnable actionOnRelease;

    protected double value1, value2;
    protected byte currentValue = 0;
    //0没有 1 第一个 2 第二个 拖动时调节的
    protected double min, max;

    // ghost slider for scrolling event
    protected double scrollHandleX1, scrollHandleX2, scrollHandleY, scrollHandleH;
    protected boolean scrollHandleMouseOver;

    protected boolean handleMouseOverH1, handleMouseOverH2;
    protected boolean currentHandleMouse = false;
    //currentHandleMouse false控制第一个 true控制第二个
    protected boolean dragging;
    protected double valueAtDragStart;

    public WRangeSlider(double value1, double value2, double min, double max) {
        this.value1 = MathHelper.clamp(value1, min, max);
        this.value2 = MathHelper.clamp(value2, min, max);
        this.min = min;
        this.max = max;
    }

    protected double handleSize() {
        return theme.textHeight();
    }

    @Override
    protected void onCalculateSize() {
        double s = handleSize();

        width = s;
        height = s;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
        if (mouseOver && !used) {
//            valueAtDragStart = value1;
            double handleSize = handleSize();

            double valueWidth = mouseX - (x + handleSize / 2);
            setLowDistance((valueWidth / (width - handleSize)) * (max - min) + min);
            if (action != null) action.run();

            dragging = true;

            double x1 = widthToXPosition(handleSize(), valueWidth(value1));
            if (mouseX >= x1 && mouseX <= x1 + height) {
                currentValue = 1;
            } else {
                double x2 = widthToXPosition(handleSize(), valueWidth(value2));
                if (mouseX >= x2 && mouseX <= x2 + height) {
                    currentValue = 2;
                }
            }

            return true;
        }


        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
        double valueWidth1 = valueWidth(value1);
        double valueWidth2 = valueWidth(value2);
        double s = handleSize();
        double s2 = s / 2;

        double x1 = widthToXPosition(s, valueWidth1);
        //手柄1位置
        double x2 = widthToXPosition(s, valueWidth2);
        //手柄2位置

        handleMouseOverH1 = mouseX >= x1 && mouseX <= x1 + height && mouseY >= y && mouseY <= y + height;
        //鼠标在手柄1/2内
        handleMouseOverH2 = mouseX >= x2 && mouseX <= x2 + height && mouseY >= y && mouseY <= y + height;
        if (!scrollHandleMouseOver) {
            scrollHandleX1 = x1;
            scrollHandleX2 = x2;
            scrollHandleY = y;
            scrollHandleH = height;
            //设置临时控件判定区域
            if (handleMouseOverH1) {
                scrollHandleMouseOver = true;
                currentHandleMouse = false;
            } else if (handleMouseOverH2) {
                scrollHandleMouseOver = true;
                currentHandleMouse = true;
            }
        } else {
            scrollHandleMouseOver = (
                (mouseX >= scrollHandleX1 && mouseX <= scrollHandleX1 + scrollHandleH) ||
                    (mouseX >= scrollHandleX2 && mouseX <= scrollHandleX2 + scrollHandleH)
            ) &&
                mouseY >= scrollHandleY &&
                mouseY <= scrollHandleY + scrollHandleH;
        }

        boolean mouseOverX = mouseX >= this.x + s2 && mouseX <= this.x + s2 + width - s;
        mouseOver = mouseOverX && mouseY >= this.y && mouseY <= this.y + height;

        if (dragging) {
            if (mouseOverX) {
                if (currentValue == 1) {
                    valueWidth1 += mouseX - lastMouseX;
                    valueWidth1 = MathHelper.clamp(valueWidth1, 0, width - s);

                    set((valueWidth1 / (width - s)) * (max - min) + min, value2);

                } else if (currentValue == 2) {
                    valueWidth2 += mouseX - lastMouseX;
                    valueWidth2 = MathHelper.clamp(valueWidth2, 0, width - s);

                    set(value1, (valueWidth2 / (width - s)) * (max - min) + min);
                }
                if (action != null) action.run();

            } else {
                if (currentValue == 1) {
                    if (value1 > min && mouseX < this.x + s2) {
                        value1 = min;
                        if (action != null) action.run();
                    } else if (value1 < max && mouseX > this.x + s2 + width - s) {
                        value1 = max;
                        if (action != null) action.run();
                    }
                } else if (currentValue == 2) {
                    if (value2 > min && mouseX < this.x + s2) {
                        value2 = min;
                        if (action != null) action.run();
                    } else if (value2 < max && mouseX > this.x + s2 + width - s) {
                        value2 = max;
                        if (action != null) action.run();
                    }
                }
            }
        }
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            if (value1 != valueAtDragStart && actionOnRelease != null) {
                actionOnRelease.run();
            }

            dragging = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseScrolled(double amount) {
        // when user starts to scroll over regular handle
        // remember it's position and check only this "ghost"
        // position to allow scroll (until it leaves ghost area)
        if (!scrollHandleMouseOver && (handleMouseOverH1 || handleMouseOverH2)) {
            scrollHandleX1 = scrollHandleX2 = x;
            scrollHandleY = y;
            scrollHandleH = height;
            scrollHandleMouseOver = true;
        }

        if (scrollHandleMouseOver) {

            if (!currentHandleMouse) {
                if (parent instanceof WIntRangeEdit)
                    set(value1 + 1 * amount, value2);
                else
                    set(value1 + 0.05 * amount, value2);
            } else {
                if (parent instanceof WIntRangeEdit)
                    set(value1, value2 + 1 * amount);
                else
                    set(value1, value2 + 0.05 * amount);

            }

            if (action != null) action.run();
            return true;
        }

        return false;
    }

    public void set(double v1, double v2) {
        value1 = Math.clamp(v1,min,max);
        value2 = Math.clamp(v2,min,max);
    }

    public void setMin(double min) {
        min = MathHelper.clamp(min, this.min, this.max);
        if (Math.max(value1, value2) < min) {
            value1 = value2 = min;
        } else if (value1 <= value2) {
            value1 = min;
        } else value2 = min;
    }

    public void setMax(double max) {
        max = MathHelper.clamp(max, this.min, this.max);
        if (Math.min(value1, value2) > max) {
            value1 = value2 = max;
        } else if (value1 > value2) {
            value1 = max;
        } else value2 = max;
    }

    /**
     * 把离此值最近的值设置为此
     *
     * @param value value
     */
    private void setLowDistance(double value) {
        value = MathHelper.clamp(value, min, max);
        if (Math.abs(value1 - value) <= Math.abs(value2 - value)) {
            valueAtDragStart = value1;
            value1 = value;
        } else {
            valueAtDragStart = value2;
            value2 = value;
        }
    }

    public double[] get() {
        return new double[]{Math.min(value1, value2), Math.max(value1, value2)};
    }


    protected double valueWidth(double value) {
        double valuePercentage = (value - min) / (max - min);
        //化为百分比值 min到 value占min到max的百分比
        return valuePercentage * (width - handleSize());//手柄会占用两端一部分
        //百分比乘总宽度得到value的位置
    }

    protected double widthToXPosition(double handleSize, double width) {
        return this.x + handleSize / 2 + width - height / 2;
        //手柄一半 减去高度一半            (控制判定点是正方形) x在判定点左边
    }
}
