//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.HFaceDirOpposite;
//
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import net.minecraft.block.BigDripleafBlock;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.registry.tag.BlockTags;
//import net.minecraft.util.math.BlockPos;
//
//public class BigDripLeafDataGetter extends HFaceDirOppositeDataGetter {
//    private static final MinecraftClient mc = MinecraftClient.getInstance();
//
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos blockPos = dirData.placePos().down();
//        BlockState blockState = mc.world.getBlockState(blockPos);
//        if ((blockState.isOf(Blocks.BIG_DRIPLEAF) || blockState.isOf(Blocks.BIG_DRIPLEAF_STEM) || blockState.isIn(BlockTags.BIG_DRIPLEAF_PLACEABLE)))
//            return super.getData(needState, dirData);
//        return PlaceData.NULL;
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//
//        return needState.getBlock() instanceof BigDripleafBlock;
//
//
//    }
//}
