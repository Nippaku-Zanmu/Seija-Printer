package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.print_main.printer.util.*;
import com.kijinseija.seija_printer.settings.impl.DoubleRangeSetting;
import com.kijinseija.seija_printer.settings.obj.DoubleRange;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ItemSearcher extends Module {
    public ItemSearcher() {
        super(Addon.CATEGORY, "ItemSearcher", "");
    }

    private final SettingGroup sgDefault = this.settings.getDefaultGroup();

    Setting<BlockPos> bpSetPoint1 = sgDefault.add(new BlockPosSetting.Builder()
        .name("block1")
        .build());
    Setting<BlockPos> bpSetPoint2 = sgDefault.add(new BlockPosSetting.Builder()
        .name("block2")
        .build());

    Thread scheAnalysis = null;

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WHorizontalList l1 = list.add(theme.horizontalList()).expandX().widget();

        WButton analysis = l1.add(theme.button("Analysis")).expandX().widget();
        analysis.action = () -> {
            if (!isActive()) return;
            if (scheAnalysis != null)
                scheAnalysis.interrupt();

            scheAnalysis = new Thread(this::analysisArea);
            scheAnalysis.start();

        };


        WHorizontalList l2 = list.add(theme.horizontalList()).expandX().widget();

        WButton print = l2.add(theme.button("Print item list")).expandX().widget();
        print.action = () -> {
            if (!isActive()) return;
            blockInfo.forEach((key, count) -> {
                MutableComponent blockCount = Component.literal("Block: ")
                    .append(Component.translatable(key.getDescriptionId()))
                    .append("Count :");
                if (count / 64 > 0) {
                    blockCount
                        .append(count / 64 + "")
                        .append(Component.literal(" *64 ").withColor(0x87CAFF))
                        //rgb(135, 202, 255)
                        .append("+ ");
                }


                blockCount.append(count % 64 + "");
                ChatUtils.sendMsg("[AdvancedPrinter]", blockCount);
            });
            //blockInfo.entrySet().forEach(b-> ChatUtils.sendMsg(++" count: "+b.getValue()));

        };
        return list;

    }

    public final Setting<DoubleRange> dRangeSetStealDelay = sgDefault.add(new DoubleRangeSetting.Builder()
        .name("steal-delay")
        .sliderRange(0, 2000)
        .range(0, 2000)
        .defaultValue(100, 200)
        .build());


    Map<Block, Integer> blockInfo = new ConcurrentHashMap<>();
//    存储方块信息,方块替换
//    HashMap<Item, Integer> itemInfo = new HashMap<>();


    private void analysisArea() {

        BlockPos min = BlockPos.min(bpSetPoint1.get(), bpSetPoint2.get());
        BlockPos max = BlockPos.max(bpSetPoint1.get(), bpSetPoint2.get());

        for (BlockPos bp : BlockPos.betweenClosed(min, max)) {
            if (Thread.currentThread().isInterrupted()) {
                ChatUtils.sendMsg("[AdvancedPrinter]", Component.nullToEmpty("Analysis stop"));
                break;
            }

            if (!BlockUtil.blockposFilter(bp)) {
                continue;
            }

            // 不是空气、液体等可被填充的
            if (BlockUtil.isCanPlaceInBlock(
                BlockReplaceUtils.getScheStateNonReplace(bp).getBlock())) {
                continue;
            }

            // 方块一致
            if (ScheVerifyMixinUtil.isReplacedBlockEqual(
                BlockReplaceUtils.getScheStateNonReplace(bp).getBlock(),
                mc.level.getBlockState(bp).getBlock())) {
                continue;
            }

            Block block = SchematicWorldHandler
                .getSchematicWorld()
                .getBlockState(bp)
                .getBlock();

            blockInfo.merge(block, 1, Integer::sum);

        }

        //拿到了需求数量后先减去背包内已有的
        for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            Block stealBlock = needSteal(stack.getItem());
            updateInfo(stealBlock, stack.getCount());
        }
        ChatUtils.sendMsg("[AdvancedPrinter]", Component.nullToEmpty("Analysis complete"));

    }

    //是否需要拿走 需要拿则返回拿的方块类型
    private Block needSteal(Item item) {
        if (item instanceof BlockItem) {
            for (Map.Entry<Block, Integer> blockIntegerEntry : blockInfo.entrySet()) {
                //方块替换检测
                Block scheBlock = blockIntegerEntry.getKey();
                List<Block> replaceBlocks = BlockReplaceUtils.INSTANCE.getReplaceBlocks(scheBlock);
                if (replaceBlocks.isEmpty()) {
                    if (scheBlock.asItem().equals(item)) return scheBlock;
                } else {
                    for (Block replaceBlock : replaceBlocks) {
                        if (replaceBlock.asItem().equals(item)) return scheBlock;
                    }
                }

            }
        }
        return null;
    }

    //减少清单内物品数量
    private void updateInfo(Block block, int count) {
        blockInfo.computeIfPresent(block, (b, i) -> {
            int now = i - count;
            return now <= 0 ? null : now;
        });
    }


    @Override
    public void onDeactivate() {
        blockInfo.clear();
        super.onDeactivate();
    }

    SeijaTimer stealTimer = new SeijaTimer(dRangeSetStealDelay.get()::nextRandom);

    private final void tick() {
        AbstractContainerMenu scrHand = mc.player.containerMenu;
        //ChatUtils.sendMsg(Text.of((scrHand instanceof GenericContainerScreenHandler) +"eq?"+ scrHand.getClass().getName()));
        //&&scrHand.getType().equals(ScreenHandlerType.GENERIC_9X3) ||scrHand.getType().equals(ScreenHandlerType.GENERIC_9X6)


        if ((scrHand instanceof ChestMenu
            || scrHand instanceof ShulkerBoxMenu) && InvUtils.findEmpty().found()) {
            if (stealTimer.passed(dRangeSetStealDelay.get().getCurrentRandom())) {
                stealTimer.reset();
                stealChest(scrHand);
            }
            //  ChatUtils.sendMsg(Text.of(stealChest(scrHand)+""));

        } else slot = 0;
    }


    int slot = 0;

    private boolean stealChest(AbstractContainerMenu scrHand) {

        while (slot < SlotUtils.indexToId(SlotUtils.MAIN_START)) {
            ItemStack stealStack = scrHand.getSlot(slot).getItem();
            Block stealBlock = needSteal(stealStack.getItem());
            if (stealBlock != null) {
                updateInfo(stealBlock, stealStack.getCount());
                InvUtils.shiftClick().slotId(slot);
                slot++;
                return true;
            }
            slot++;
        }
        return false;
    }

    @EventHandler
    public void onRender3d(Render3DEvent e) {
        tick();
        BlockPos p1 = bpSetPoint1.get();
        BlockPos p2 = bpSetPoint2.get();

        if (p1 == null && p2 == null) return;

        // 颜色（浅色 + 半透明）
        Color p1Line = new Color(255, 80, 80, 180);
        Color p1Fill = new Color(255, 80, 80, 60);

        Color p2Line = new Color(80, 80, 255, 180);
        Color p2Fill = new Color(80, 80, 255, 60);

        Color regionLine = new Color(255, 255, 255, 200);

        Renderer3D r = e.renderer;


        // 选点 1（红）
        if (p1 != null) {
            int x = p1.getX(), y = p1.getY(), z = p1.getZ();

            r.boxSides(x, y, z, x + 1, y + 1, z + 1, p1Fill, 0);
            r.boxLines(x, y, z, x + 1, y + 1, z + 1, p1Line, 0);
        }

        // 选点 2（蓝）
        if (p2 != null) {
            int x = p2.getX(), y = p2.getY(), z = p2.getZ();

            r.boxSides(x, y, z, x + 1, y + 1, z + 1, p2Fill, 0);
            r.boxLines(x, y, z, x + 1, y + 1, z + 1, p2Line, 0);
        }

        // 区域外框（白线）
        if (p1 != null && p2 != null) {
            int minX = Math.min(p1.getX(), p2.getX());
            int minY = Math.min(p1.getY(), p2.getY());
            int minZ = Math.min(p1.getZ(), p2.getZ());

            int maxX = Math.max(p1.getX(), p2.getX()) + 1;
            int maxY = Math.max(p1.getY(), p2.getY()) + 1;
            int maxZ = Math.max(p1.getZ(), p2.getZ()) + 1;

            r.boxLines(
                minX, minY, minZ,
                maxX, maxY, maxZ,
                regionLine,
                0
            );
        }

        r.render(e.matrices);
    }
}
