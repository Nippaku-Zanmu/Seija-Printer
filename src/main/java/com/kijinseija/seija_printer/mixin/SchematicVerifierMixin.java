/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.mixin;

import com.google.common.collect.ArrayListMultimap;
import com.kijinseija.seija_printer.print_main.printer.util.ScheVerifyMixinUtil;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.schematic.verifier.SchematicVerifier;
import fi.dy.masa.litematica.util.ItemUtils;
import fi.dy.masa.litematica.world.WorldSchematic;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.*;

import java.util.HashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static com.kijinseija.seija_printer.print_main.printer.util.ScheVerifyMixinUtil.isReplacedBlockEqual;

@Mixin(SchematicVerifier.class)
public abstract class SchematicVerifierMixin {
    @Shadow(remap = false)
    @Final
    private static MutablePair<BlockState, BlockState> MUTABLE_PAIR;
    @Shadow(remap = false)
    @Final
    private HashSet<Pair<BlockState, BlockState>> ignoredMismatches;

    @Shadow(remap = false)
    @Final
    private ArrayListMultimap<Pair<BlockState, BlockState>, BlockPos> missingBlocksPositions;
    @Shadow(remap = false)
    @Final
    private ArrayListMultimap<Pair<BlockState, BlockState>, BlockPos> wrongBlocksPositions;
    @Shadow(remap = false)
    @Final
    private ArrayListMultimap<Pair<BlockState, BlockState>, BlockPos> wrongStatesPositions;
    @Shadow(remap = false)
    @Final
    private ArrayListMultimap<Pair<BlockState, BlockState>, BlockPos> extraBlocksPositions;
    @Shadow(remap = false)
    @Final
    private Object2IntOpenHashMap<BlockState> correctStateCounts;

    @Shadow(remap = false)
    @Final
    private Object2ObjectOpenHashMap<BlockPos, SchematicVerifier.BlockMismatch> blockMismatches;

    @Shadow
    private ClientLevel worldClient;
    @Shadow(remap = false)
    private WorldSchematic worldSchematic;
    @Shadow(remap = false)
    private int correctStatesCount;



//    @Redirect(method = "checkBlockStates",at = @At())



    /**
     * @author Nippaku_Zanmu
     * @reason 实现方块替换后正常渲染
     */
    @Overwrite(remap = false)
    private void checkBlockStates(int x, int y, int z, BlockState stateSchematic, BlockState stateClient) {
        System.out.println("check");
        BlockPos pos = new BlockPos(x, y, z);
        boolean replacedEqual = isReplacedBlockEqual(stateSchematic.getBlock(), stateClient.getBlock());

        if ((!(replacedEqual
            && ScheVerifyMixinUtil.propEqual(stateSchematic,stateClient)))
            && (!stateClient.isAir() || !stateSchematic.isAir())) {
            MUTABLE_PAIR.setLeft(stateSchematic);
            MUTABLE_PAIR.setRight(stateClient);

            if (!this.ignoredMismatches.contains(MUTABLE_PAIR)) {
                SchematicVerifier.BlockMismatch mismatch = null;

                if (!stateSchematic.isAir()) {
                    if (stateClient.isAir()) {
                        mismatch = new SchematicVerifier.BlockMismatch(SchematicVerifier.MismatchType.MISSING, stateSchematic, stateClient, 1);
                        this.missingBlocksPositions.put(Pair.of(stateSchematic, stateClient), pos);
                    } else {
                        //投影这边只考虑替换表内的
                        //默认替换是为了放置正确方块 桥模式搭的桥是要拆掉的 没必要修改渲染状态
                        if (!replacedEqual) {
                            mismatch = new SchematicVerifier.BlockMismatch(SchematicVerifier.MismatchType.WRONG_BLOCK, stateSchematic, stateClient, 1);
                            this.wrongBlocksPositions.put(Pair.of(stateSchematic, stateClient), pos);
//                            System.out.println("PutWB"+pos);
                        } else {
                            mismatch = new SchematicVerifier.BlockMismatch(SchematicVerifier.MismatchType.WRONG_STATE, stateSchematic, stateClient, 1);
                            this.wrongStatesPositions.put(Pair.of(stateSchematic, stateClient), pos);
                        }
                    }
                } else if (!Configs.Visuals.IGNORE_EXISTING_FLUIDS.getBooleanValue() || !stateClient.liquid()) {
                    mismatch = new SchematicVerifier.BlockMismatch(SchematicVerifier.MismatchType.EXTRA, stateSchematic, stateClient, 1);
                    this.extraBlocksPositions.put(Pair.of(stateSchematic, stateClient), pos);
                }

                if (mismatch != null) {
                    this.blockMismatches.put(pos, mismatch);

                    ItemUtils.setItemForBlock(this.worldClient, pos, stateClient);
                    ItemUtils.setItemForBlock(this.worldSchematic, pos, stateSchematic);
                }
            }
        } else {
            ItemUtils.setItemForBlock(this.worldClient, pos, stateClient);
            this.correctStateCounts.addTo(stateClient, 1);

            if (!stateSchematic.isAir()) {
                ++this.correctStatesCount;
            }
        }
    }

}
