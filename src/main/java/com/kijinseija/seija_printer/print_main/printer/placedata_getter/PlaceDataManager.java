package com.kijinseija.seija_printer.print_main.printer.placedata_getter;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.HFaceDirOpposite.BigDripLeafDataGetter;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.HFaceDirOpposite.HFaceDirOppositeDataGetter;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.HFaceDirOpposite.RedStoneGateDataGetter;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.HFaceDirOpposite.SmallDripDataGetter;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.DataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.*;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceDataPack;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class PlaceDataManager {
    public static final PlaceDataManager INSTANCE = new PlaceDataManager();
    private static final Printer pri = Printer.getINSTANCE();
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static PlaceDataPack getPlaceData(BlockPos pos, BlockState needState) {
        List<Direction> dirs = BlockUtil.getDirs(pos);
        List<ItemStack> stacks;
        if (/*dirs.isEmpty() ||*/ (stacks = InvUtil.getBlockStacks(needState.getBlock())).size() == 0) {
            //没有可用Facing(后移至原版计算(for))//找不到方块//不可放置
            return PlaceDataPack.NULL;
        }
        return PlaceDataManager.INSTANCE.getPlaceData(pos, needState, dirs, stacks);
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

//        if (true)
//            return DataGetter.getData(needState, new DirData(pos, dirs), stacks.get(0));

        if (pri.enablePrecisionPlace.get())//是否启用精准放置
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

        if (pri.enablePrecisionPlace.get() && pri.tryVanillaPrecisionPlace.get()) {
            return DataGetter.getData(needState, new DirData(pos, dirs), stacks.get(0));
        }
        for (Direction dir : dirData.dirs()) {
            for (Vec3d hitVec : dirData.clickVecs(dir)) {
                return PlaceDataPack.plac(new PlaceData(pos.offset(dir), dir.getOpposite()
                    , hitVec, true, null));
            }
        }
        return PlaceDataPack.NULL;

    }

    private final List<AbstractDataGetter> dataGetters = new ArrayList<>();

    private PlaceDataManager() {
        //精准放置规则注册表
//        dataGetters.add(new ClickDirDataGetter());
//        dataGetters.add(new HFaceDirOppositeDataGetter());
//        dataGetters.add(new RedStoneGateDataGetter());
//        dataGetters.add(new BigDripLeafDataGetter());
//        dataGetters.add(new SmallDripDataGetter());
//        dataGetters.add(new PillarDataGetter());
        dataGetters.add(new PistonDataGetter());
        dataGetters.add(new SlabDataGetter());
//        dataGetters.add(new StairDataGetter());
//        dataGetters.add(new TrapdoorDataGetter());
//       //dataGetters.add(new WallMountedDataGetter());
//        dataGetters.add(new BedDataGetter());
//        dataGetters.add(new DoorDataGetter());
//        dataGetters.add(new HopperDataGetter());
//        dataGetters.add(new HFaceDirDataGetter());
        dataGetters.add(new ChestDataGetter());
//        dataGetters.add(new AnvilDataGetter());
//        dataGetters.add(new TorchBlockDataGetter());
        dataGetters.add(new ObserverDataGetter());
//        dataGetters.add(new SignDataGetter());
//        dataGetters.add(new HangingSignDataGetter());
    }
}
