package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  方块替换计算
 *  用于计算投影中某位置方块状态应该替换为啥样的
 */
public class BlockReplaceUtils {
    public static final BlockReplaceUtils INSTANCE = new BlockReplaceUtils();
    static MinecraftClient mc = MinecraftClient.getInstance();
    Printer pri = Printer.getINSTANCE();


    /**
     * 桥模式时计算周围方块是否需要支持时使用
     *
     * @param pos pos
     * @return {@link BlockState}
     * @see BlockState
     */


    public BlockState getScheState(BlockPos pos) {
        return replaceState(SchematicWorldHandler.getSchematicWorld().getBlockState(pos), pos);
    }

    public BlockState replaceState(BlockState state, BlockPos pos) {

        BlockState repState = needBlockReplace(state, pos,true).getDefaultState();
        try {
            for (Property property : state.getProperties()) {
                repState = repState.with(property, state.get(property));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repState;
    }

    private Block needBlockReplace(BlockState bs, BlockPos pos,boolean calcBridge) {
        List<Block> blocks = pri.replaceMap.get(bs.getBlock());
        if (blocks != null && blocks.size() != 0)
            for (Block b : blocks) {
                if (InvUtils.find(Item.BLOCK_ITEMS.get(b)).found())
                    return b;
            }
        if (calcBridge) {
            Block bridgedBlockReplace = BlockReplaceUtils.INSTANCE.bridgeBlockReplace(bs, pos);
            if (bridgedBlockReplace != null)
                return bridgedBlockReplace;
        }


        return bs.getBlock();
    }
    public BlockState normalReplaceState(BlockState state) {

        Block replaceBlock = normalReplaceBlock(state);
        if (replaceBlock == null) return state;
        BlockState repState = replaceBlock.getDefaultState();
        try {
            for (Property property : state.getProperties()) {
                repState = repState.with(property, state.get(property));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repState;
    }

    public Block normalReplaceBlock(BlockState bs) {

        if (bs.getBlock() instanceof FlowerPotBlock)
            return Blocks.FLOWER_POT;
        else if (bs.getBlock() instanceof PillarBlock && InvUtil.findItem(stack -> stack.getItem() instanceof AxeItem) && (!InvUtil.findBlock(bs.getBlock()))) {
            return strippedMap.get(bs.getBlock());
        } else if (bs.getBlock() instanceof FarmlandBlock || bs.getBlock() instanceof DirtPathBlock) {
            for (Block dirt : DIRTS) {
                if (InvUtil.findBlock(dirt))
                    return dirt;
            }
        }
        return null;
    }

    public static final Block[] DIRTS = new Block[]{Blocks.DIRT, Blocks.GRASS_BLOCK};


    public Block bridgeBlockReplace(BlockState bs, BlockPos pos) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);

        if (
            pri.bSetBridgeMode.get()//开了桥模式
                && BlockUtil.isCanPlaceInBlock(bs.getBlock())//替换的位置原本需要方块为空
                && pos != null
                && BlockUtil.canPlaceIn(pos)//实际也为空
                && (!BlockUtil.getDirs(pos).isEmpty())//有点击方位
                && (!interactDir.isEmpty()))//有用
        {
            for (Direction direction : interactDir) {
                if (pri.liSetBridgeDirs.get().contains(direction)//是可以用的方位
                    && !BlockUtil.isCanPlaceInBlock(getScheStateBridegeMode(pos.offset(direction)).getBlock())
                    //被支持的方块是投影中是需要放置的方块
                    && BlockUtil.getDirs(pos.offset(direction)).isEmpty()//被支持的方块不能直接放置
                    &&BlockUtil.canPlaceIn(pos.offset(direction))//被支持的方块还没被放置
                ) {
                    //可用支撑
                    for (Block block : pri.liSetBridgeBlocks.get()) {
                        if (InvUtil.findBlock(block)) return block;
                    }
                    return null;
                }
            }

        }
        return null;
    }

    private BlockState getScheStateBridegeMode(BlockPos pos){
        BlockState state = SchematicWorldHandler.getSchematicWorld().getBlockState(pos);
        BlockState repState = needBlockReplace(state, pos,false).getDefaultState();
        try {
            for (Property property : state.getProperties()) {
                repState = repState.with(property, state.get(property));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repState;
    }





    public final Map<Block, Block> strippedMap = new HashMap<>();
    //把mc的Map反过来 方便查询
    private void initMap() {
        for (Map.Entry<Block, Block> blockBlockEntry : AxeItem.STRIPPED_BLOCKS.entrySet()) {
            strippedMap.put(blockBlockEntry.getValue(), blockBlockEntry.getKey());
        }
    }

    private BlockReplaceUtils() {
        initMap();
    }

}
