package com.kijinseija.seija_printer.mixin;

import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WTextBox.class,remap = false)
public interface WTextBoxAccessor {
    @Accessor
    public int getCursor();
}
