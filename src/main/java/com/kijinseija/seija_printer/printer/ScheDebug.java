package com.kijinseija.seija_printer.printer;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.printer.util.BlockReplaceUtils;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ScheDebug extends Module {
    public ScheDebug() {
        super(Addon.CATEGORY, "ScheDebug", "");
    }
    SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<BlockPos> posSetting = sgDefault.add(new BlockPosSetting.Builder()
        .name("Pos")
        .build());

    @Override
    public void onActivate() {
        ChatUtils.sendMsg((Text) BlockReplaceUtils.INSTANCE.getScheState(posSetting.get()).getBlock());
    }
}
