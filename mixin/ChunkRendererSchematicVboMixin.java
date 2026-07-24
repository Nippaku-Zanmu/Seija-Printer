/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.mixin;

import com.kijinseija.seija_printer.print_main.printer.util.ScheVerifyMixinUtil;
import fi.dy.masa.litematica.render.schematic.ChunkRendererSchematicVbo;
import fi.dy.masa.litematica.util.OverlayType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ChunkRendererSchematicVbo.class,remap = false)
public class ChunkRendererSchematicVboMixin {
    @Shadow
    private boolean ignoreClientWorldFluids;

    /**
     * @author Nippaku_Zanmu
     * @reason 实现方块替换后正常渲染
     */
    @Overwrite
    protected OverlayType getOverlayType(BlockState stateSchematic, BlockState stateClient)
    {
        boolean replacedBlockEqual = ScheVerifyMixinUtil.isReplacedBlockEqual(stateSchematic.getBlock(), stateClient.getBlock());
        if (replacedBlockEqual
        &&ScheVerifyMixinUtil.propEqual(stateSchematic,stateClient))
        {
            return OverlayType.NONE;
        }
        else
        {
            boolean clientHasAir = stateClient.isAir();
            boolean schematicHasAir = stateSchematic.isAir();

            if (schematicHasAir)
            {
                return (clientHasAir || (this.ignoreClientWorldFluids && stateClient.liquid())) ? OverlayType.NONE : OverlayType.EXTRA;
            }
            else
            {
                if (clientHasAir || (this.ignoreClientWorldFluids && stateClient.liquid()))
                {
                    return OverlayType.MISSING;
                }
                // Wrong block
                else if (!replacedBlockEqual)
                {
                    return OverlayType.WRONG_BLOCK;
                }
                // Wrong state
                else
                {
                    return OverlayType.WRONG_STATE;
                }
            }
        }
    }
}
