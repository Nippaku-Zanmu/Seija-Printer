/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.extra_setting.HasExtraSetting;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.DataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.*;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceDataPack;
import com.kijinseija.seija_printer.settings.core.SettingGroup;
import com.kijinseija.seija_printer.settings.core.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceDataManager implements HasExtraSetting {
    private static final PlaceDataManager INSTANCE = new PlaceDataManager();
    public static final PlaceDataManager getInstance(){
        return INSTANCE;
    }


    private static final Minecraft mc = Minecraft.getInstance();

    public PlaceDataPack getPlaceData(BlockPos pos, BlockState needState) {
        if (!mc.level.getBlockState(pos).canBeReplaced())
            return PlaceDataPack.NULL;
        //若某位置已有不可被替换的方块 则返回
        if (needState.isAir())
            return PlaceDataPack.NULL;
        //若投影中某位置为空气,则返回
        List<Direction> dirs = BlockUtil.getSortedDirs(pos,false);
        List<ItemStack> stacks;
        if (/*dirs.isEmpty() ||*/ needState.getBlock() instanceof AirBlock || (stacks = InvUtil.getBlockStacks(needState.getBlock())).isEmpty()) {
            //没有可用Facing(后移至原版计算(for))//找不到方块//不可放置

            return PlaceDataPack.NULL;

        }
        return getPlaceData(pos, needState, dirs, stacks);
    }

    /**
     * get place data 获取精准放置的数据
     *
     * @param pos       pos 放置位置
     * @param needState needState 需要的方块状态
     * @param dirs      dirs 所有可用支持面
     * @return {@link PlaceData} 返回放置数据
     * @see PlaceData
     */
    public PlaceDataPack getPlaceData(BlockPos pos, BlockState needState, List<Direction> dirs, List<ItemStack> stacks) {
        DirData dirData = new DirData(pos, dirs);
        //ChatUtils.sendMsg(Text.of("GetData2"));
//        if (true)
//            return DataGetter.getData(needState, new DirData(pos, dirs), stacks.get(0));
        Printer pri = Printer.INSTANCE;
        if (pri.bSetEnablePrecisionPlace.get())//是否启用精准放置
            try {
                for (AbstractDataGetter dataGetter : dataGetters) {
                    //遍历所有数据获取器,以求更加精准的放置数据
                    if (dataGetter.isSuitable(needState, pos)) {
                        //适合则进行数据获取
                        return PlaceDataPack.plac(dataGetter.getData(needState, dirData));
                    }
                }
            } catch (IllegalArgumentException ignore) {
                //异常处理,防止小天才乱玩方块替换
            }
        //默认放置
        if (pri.bSetEnablePrecisionPlace.get() && pri.bSetTryVanillaPrecisionPlace.get()) {
            return DataGetter.getData(needState, new DirData(pos, dirs), stacks.get(0));
        }
        for (Direction dir : dirData.dirs()) {
            for (Vec3 hitVec : dirData.clickVecs(dir)) {
                return PlaceDataPack.plac( PlaceData.newInstance(pos.relative(dir), dir.getOpposite()
                    , hitVec, true, null));
            }
        }
        return PlaceDataPack.NULL;

    }

    private final List<AbstractDataGetter> dataGetters = new ArrayList<>();

    private PlaceDataManager() {
        //精准放置规则注册表
        dataGetters.add(new BuckedDataGetter());
//        dataGetters.add(new ClickDirDataGetter());
//        dataGetters.add(new HFaceDirOppositeDataGetter());
//        dataGetters.add(new RedStoneGateDataGetter());
//        dataGetters.add(new BigDripLeafDataGetter());
//        dataGetters.add(new SmallDripDataGetter());
//        dataGetters.add(new PillarDataGetter());
//        dataGetters.add(new PistonDataGetter());
//        dataGetters.add(new SlabDataGetter());
//        dataGetters.add(new StairDataGetter());
//        dataGetters.add(new TrapdoorDataGetter());
//       //dataGetters.add(new WallMountedDataGetter());
//        dataGetters.add(new BedDataGetter());
//        dataGetters.add(new DoorDataGetter());
//        dataGetters.add(new HopperDataGetter());
//        dataGetters.add(new HFaceDirDataGetter());
//        dataGetters.add(new ChestDataGetter());
//        dataGetters.add(new AnvilDataGetter());
//        dataGetters.add(new TorchBlockDataGetter());
//        dataGetters.add(new ObserverDataGetter());
//        dataGetters.add(new SignDataGetter());
//        dataGetters.add(new HangingSignDataGetter());

    }

    @Override
    public SettingGroup getSettingGroup(Settings settings) {
        SettingGroup sgDatagetter = settings.createGroup("PlaceDataGetter");
        for (AbstractDataGetter dataGetter : dataGetters) {
            Arrays.stream(dataGetter.getSettings()).forEach(sgDatagetter::add);
        }
        return sgDatagetter;
    }
}
