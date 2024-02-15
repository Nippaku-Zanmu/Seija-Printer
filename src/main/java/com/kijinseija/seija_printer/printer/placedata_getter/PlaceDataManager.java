package com.kijinseija.seija_printer.printer.placedata_getter;

import com.kijinseija.seija_printer.printer.Printer;
import com.kijinseija.seija_printer.printer.util.BlockReplaceUtils;
import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.getter.*;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class PlaceDataManager {
    public static final PlaceDataManager INSTANCE = new PlaceDataManager();

    /**
     * get place data 获取精准放置的数据
     *
     * @param pos       pos 放置位置
     * @param needState needState 需要的方块状态
     * @param dirs      dirs 所有可用支持面
     * @return {@link PlaceData} 返回放置数据
     * @see PlaceData
     */
    public PlaceData getPlaceData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        if (Printer.getINSTANCE().enablePrecisionPlace.get())//是否启用精准放置
            try {
                for (AbstractDataGetter dataGetter : dataGetters) {
                    //遍历所有数据获取器,以求更加精准的放置数据
                    if (dataGetter.isSuitable(needState, pos)) {
                        //适合则进行数据获取
                        return dataGetter.getData(pos, needState, dirs);
                    }
                }
            } catch (IllegalArgumentException ignore) {
                //异常处理,防止小天才乱玩方块替换
            }
        //默认放置
        return new PlaceData(pos.offset(dirs.get(0)), dirs.get(0).getOpposite()
            , pos.toCenterPos().offset(dirs.get(0), 0.5), true, null);
    }

    //todo 白嫖原版方法 通过继承ItemPlacementContext,重写方法修改玩家
    private final List<AbstractDataGetter> dataGetters = new ArrayList<>();

    private PlaceDataManager() {
        //精准放置注册表
        dataGetters.add(new ClickDirDataGetter());
        dataGetters.add(new HFaceDirOppositeDataGetter());
        dataGetters.add(new PillarDataGetter());
        dataGetters.add(new PistonDataGetter());
        dataGetters.add(new SlabDataGetter());
        dataGetters.add(new StairDataGetter());
        dataGetters.add(new TrapdoorDataGetter());
        dataGetters.add(new WallMountedDataGetter());
        dataGetters.add(new BedDataGetter());
        dataGetters.add(new DoorDataGetter());
        dataGetters.add(new HopperDataGetter());
        dataGetters.add(new HFaceDirDataGetter());
        dataGetters.add(new ChestDataGetter());
        dataGetters.add(new AnvilDataGetter());
        dataGetters.add(new TorchBlockDataGetter());
        dataGetters.add(new ObserverDataGetter());
        dataGetters.add(new SignDataGetter());
    }
}
