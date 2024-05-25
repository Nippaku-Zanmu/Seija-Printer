package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.loader.LoaderAntiCrash;
import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.block_fixer.FixerManager;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.PlaceDataManager;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.FakePlacementContext;
import com.kijinseija.seija_printer.print_main.printer.util.*;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceDataPack;
import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import com.kijinseija.seija_printer.settings.DirectionListSetting;
import fi.dy.masa.litematica.data.DataManager;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class Printer extends LoaderAntiCrash {
    //todo 切换智能排序
    public static Printer getINSTANCE() {
        return INSTANCE;
    }

    private static Printer INSTANCE = new Printer();

    public Printer() {
        super(Addon.CATEGORY, "Seija-litematica-printer", "Automatically prints open schematics");
        INSTANCE = this;
    }

    private final SettingGroup sgBasicCalc = settings.createGroup("BasicCalc");
    public final Setting<Double> dSetPrintingRange = sgBasicCalc.add(new DoubleSetting.Builder()
        .name("PrintingRange")
        .description("The block place range.")
        .defaultValue(4.7)
        .min(0).sliderMin(0)
        .sliderMax(6)
        .build()
    );
    public final Setting<Double> dSetPrintingYDistance = sgBasicCalc.add(new DoubleSetting.Builder()
        .name("PrintingYDistance")
        .description("Maximum depth.")
        .defaultValue(2.5)
        .min(0).sliderMin(0)
        .sliderMax(6)
        .build()
    );
    public final Setting<Double> dSetAntiReplaceTime = sgBasicCalc.add(new DoubleSetting.Builder()
        .name("AntiReplaceTime")
        .description("")
        .defaultValue(150)
        .min(0).sliderMin(0)
        .sliderMax(1000)
        .build()
    );
    private final Setting<Integer> iSetPrintingDelay = sgBasicCalc.add(new IntSetting.Builder()
        .name("PrintingDelay")
        .description("Delay between printing blocks in ticks.")
        .defaultValue(51)
        .min(0).sliderMin(0)
        .max(10000).sliderMax(1000)
        .build()
    );
    private final Setting<Integer> iSetBlockPreTick = sgBasicCalc.add(new IntSetting.Builder()
        .name("BlockPreTick")
        .defaultValue(1)
        .min(0).sliderMin(0)
        .max(10000).sliderMax(10)
        .build()
    );

    public final Setting<Boolean> bSetAirPlace = sgBasicCalc.add(new BoolSetting.Builder()
        .name("Air-Place")
        .description("Allow the bot to place in the air.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> bSetLiquidInt = sgBasicCalc.add(new BoolSetting.Builder()
        .name("LiquidInteract")
        .description("Allow the printer to place on the Liquid.")
        .defaultValue(false)
        .build()
    );

    private final SettingGroup sgACBypass = settings.createGroup("AC-Bypass");

    public final Setting<Boolean> bSetRotate = sgACBypass.add(new BoolSetting.Builder()
        .name("Rotate")
        .defaultValue(true)
        .build());

    public final Setting<Boolean> bSetStrictDir = sgACBypass.add(new BoolSetting.Builder()
        .name("Strict Direction")
        .description("Doesn't place on faces which aren't in your direction.")
        .defaultValue(true)
        .build());

    public final Setting<Boolean> bSetStrictVec = sgACBypass.add(new BoolSetting.Builder()
        .name("Strict ClickVec")
        .visible(() -> !bSetAirPlace.get())
        .defaultValue(true)
        .build());
    public final Setting<Boolean> bSetRandomOffset = sgACBypass.add(new BoolSetting.Builder()
        .name("randomOffsetVec")
        .defaultValue(true)
        .build());
    public final Setting<Boolean> bSetMultiDetection = sgACBypass.add(new BoolSetting.Builder()
        .name("Multi-focus detection")
        .defaultValue(false)
        .build());

    public final Setting<Boolean> bSetRayTrace = sgACBypass.add(new BoolSetting.Builder()
        .name("rayTrace")
        .defaultValue(true)
        .visible(bSetStrictVec::get)
        .build());
    public final Setting<Boolean> bSetIgnoreEntity = sgACBypass.add(new BoolSetting.Builder()
        .name("ignoreEntityRay")
        .defaultValue(false)
        .visible(() -> bSetRayTrace.isVisible() && bSetRayTrace.get())
        .build());


    SettingGroup sgSort = settings.createGroup("Sort");

    public enum DistanceMode {
        LOW, HIGH, NONE
    }

    public final Setting<DistanceMode> eSetAngleSortMode = sgSort.add(new EnumSetting.Builder<DistanceMode>()
        .name("angleMode").defaultValue(DistanceMode.LOW).build());
    public final Setting<DistanceMode> eSetDistanceSortMode = sgSort.add(new EnumSetting.Builder<DistanceMode>()
        .name("DistanceMode").defaultValue(DistanceMode.HIGH).build());

    SettingGroup sgItemSwitch = settings.createGroup("ItemSwitch");

    public enum InvSwitchMode {
        NONE, SWAP, PICK
    }

    public final Setting<InvSwitchMode> eSetInvSwitchMode = sgItemSwitch.add(new EnumSetting.Builder<InvSwitchMode>()
        .name("InvSwitchMode")
        .defaultValue(InvSwitchMode.SWAP)
        .build()
    );
    public final Setting<Boolean> bSetAntiWrongBlock = sgItemSwitch.add(new BoolSetting.Builder()
        .name("AntiWrongBlock")
        .defaultValue(false)
        .build());
    public final Setting<Boolean> bSetIndirectInvSwap = sgItemSwitch.add(new BoolSetting.Builder()
        .name("IndirectInvSwap")
        .defaultValue(false)
        .build());
    public final Setting<String> sSetInvSwapSlot = sgItemSwitch.add(new StringSetting.Builder()
        .name("InvSwapSlot")
        .visible(bSetIndirectInvSwap::get)
        .defaultValue("4,5,6,7,8")
        .build());
    SettingGroup sgAdvancedSettings = settings.createGroup("AdvancedSettings");
    public final Setting<Boolean> bSetIllegalRotate = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("illegalRotate")
        .defaultValue(false)
        .build());

    public final Setting<Integer> iSetPredTick = sgAdvancedSettings.add(new IntSetting.Builder()

        .name("RotatePredTick")
        .defaultValue(1)
        .min(0).sliderMin(0)
        .max(10000).sliderMax(16)
        .build()
    );


    public final Setting<Boolean> bSetSneak = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("SneakPlace")
        .defaultValue(true)
        .build());

    public final Setting<Boolean> bSetPacketRotate = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("PacketRotate")
        .visible(bSetRotate::get)
        .defaultValue(false)
        .build());
    public final Setting<Boolean> bSetPacketPlace = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("PacketPlace")
        .defaultValue(false)
        .build());

    public enum SurfaceModes {
        MANHATTAN, CHEBYSHEV
    }

    public final Setting<SurfaceModes> eSetSurfaceMode = sgAdvancedSettings
        .add(new EnumSetting.Builder<SurfaceModes>()
            .name("SurfaceMode")
            .defaultValue(SurfaceModes.CHEBYSHEV)
            .build());

    public final Setting<Integer> iSetSurfaceSize = sgAdvancedSettings.add(new IntSetting.Builder()
        .name("SurfaceSize")
        .defaultValue(0)
        .min(0).sliderMin(0)
        .max(10).sliderMax(2)
        .build()
    );
    public final Setting<Boolean> bSetBridgeMode = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("BridgeMode")
        .defaultValue(false)
        .build());
    public final Setting<List<Block>> liSetBridgeBlocks = sgAdvancedSettings.add(new BlockListSetting.Builder()
        .name("BridgeBlocks")
        .visible(bSetBridgeMode::get)
        .defaultValue(Blocks.SLIME_BLOCK)
        .build()
    );
    public final Setting<List<Direction>> liSetBridgeDirs = sgAdvancedSettings.add(new DirectionListSetting.Builder()
        .name("BridgeDirection")
        .visible(bSetBridgeMode::get)
        .defaultValue(Direction.UP)
        .build());


    private final Setting<List<Block>> liSetBlackLists = sgAdvancedSettings.add(new BlockListSetting.Builder()
        .name("BlackList")
        .description("Black List.")
        .build()
    );

    public final Setting<Boolean> bSetEnablePrecisionPlace = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("enablePrecisionPlace")
        .defaultValue(true)
        .build());
    public final Setting<Boolean> bSetTryVanillaPrecisionPlace =
        sgAdvancedSettings.add(new BoolSetting.Builder()
            .name("TryVanillaPrecision")
            .visible(bSetEnablePrecisionPlace::get)
            .defaultValue(true)
            .build());
    public final Setting<Boolean> bSetEnableBlockFixer = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("enableBlockFixer")
        .defaultValue(true)
        .build());


    private final SettingGroup sgRendering = settings.createGroup("Rendering");

    public enum RenderMode {
        NONE, MULTI, ANIMATION, DEBUG
    }

    public final Setting<RenderMode> eSetRenderMode = sgRendering.add(new EnumSetting.Builder<RenderMode>()
        .name("RenderMode").defaultValue(RenderMode.MULTI).build());
    public final Setting<Double> dSetRenderTime = sgRendering.add(new DoubleSetting.Builder()
        .name("renderTime")
        .visible(() -> eSetRenderMode.get() == RenderMode.MULTI || eSetRenderMode.get() == RenderMode.DEBUG)
        .defaultValue(500)
        .min(0).sliderMin(0)
        .sliderMax(2000)
        .build()
    );
    public final Setting<Double> dSetAnimationSpeed = sgRendering.add(new DoubleSetting.Builder()
        .name("AnimationSpeed")
        .visible(() -> eSetRenderMode.get() == RenderMode.ANIMATION)
        .sliderRange(0, 100)
        .range(0, 100)
        .defaultValue(10)
        .build());
    public final Setting<Double> dSetSizeExpandMultiplier = sgRendering.add(new DoubleSetting.Builder()
        .name("SizeExpandMultiplier")
        .visible(() -> eSetRenderMode.get() == RenderMode.ANIMATION)
        .sliderRange(0, 10)
        .range(0, 100)
        .defaultValue(1.5)
        .build());
    public final Setting<Double> dSetSizeShrinkMultiplier = sgRendering.add(new DoubleSetting.Builder()
        .name("SizeShrinkMultiplier")
        .visible(() -> eSetRenderMode.get() == RenderMode.ANIMATION)
        .sliderRange(0, 10)
        .range(0, 100)
        .defaultValue(1)
        .build());

    private final Setting<Double> dSetRainbowSpeed = sgRendering.add(new DoubleSetting.Builder()
        .name("rainbowSpeed")
        .sliderRange(0, 20)
        .defaultValue(1)
        .min(0)
        .visible(() -> eSetRenderMode.get() == RenderMode.MULTI)
        .build());
    public final Setting<Boolean> bSetRenderFill = sgRendering.add(new BoolSetting.Builder()
        .name("render-fill")
        .defaultValue(true)
        .visible(() -> eSetRenderMode.get() != RenderMode.NONE)
        .build()
    );

    public final Setting<SettingColor> colSetFillColor = sgRendering.add(new ColorSetting.Builder()
        .name("colour")
        .description("The cubes colour.")
        .defaultValue(new SettingColor(95, 190, 100))
        .visible(bSetRenderFill::isVisible)
        .build()
    );
    public final Setting<Boolean> bSetRenderOutline = sgRendering.add(new BoolSetting.Builder()
        .name("outline")
        .defaultValue(true)
        .visible(() -> eSetRenderMode.get() != RenderMode.NONE)
        .build()
    );
    public final Setting<SettingColor> colSetOutLineColor = sgRendering.add(new ColorSetting.Builder()
        .name("outLine")
        .description("The cubes outline colour.")
        .defaultValue(new SettingColor(95, 190, 255))
        .visible(bSetRenderOutline::isVisible)
        .build()
    );
    private final SettingGroup sgReplaceBlockFile = settings.createGroup("ReplaceBlock");
    private final Setting<String> sSetReplaceBlockFile =
        sgReplaceBlockFile.add(new StringSetting.Builder()
            .name("replaceBlockFile")
            .defaultValue("D://a.txt")
            .build());


    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WButton start = list.add(theme.button("Load!")).expandX().widget();
        start.action = () -> new Thread(() -> loadMap(sSetReplaceBlockFile.get())).start();

        return list;
    }

    private void loadMap(String file) {
        BufferedReader br = null;
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            replaceMap.clear();
            br.lines().forEach(s -> {
                String[] sp1 = s.split(":");
                if (sp1.length != 2) return;
                Block rep = Registries.BLOCK.get(new Identifier(sp1[0]));
                if (blockCheck(rep)) return;
                List<Block> repBlocks = new ArrayList<>();
                String[] blocks = sp1[1].split(",");

                for (String blockStr : blocks) {
                    Block block = Registries.BLOCK.get(new Identifier(blockStr));
                    if (blockCheck(block)) return;
                    repBlocks.add(block);
                }
                replaceMap.put(rep, repBlocks);
            });

        } catch (FileNotFoundException e) {
            ChatUtils.sendMsg(Text.of("Error"));
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ignored) {

            }
        }
    }

    public boolean blockCheck(Block b) {
        return (b == null || b == Blocks.AIR);
    }

    public final HashMap<Block, List<Block>> replaceMap = new HashMap<>();


    SeijaTimer timer = new SeijaTimer();
    public final List<PosInfo> blackList = Collections.synchronizedList(new ArrayList<>());

    private boolean isInBlackList(BlockPos pos) {
        if (liSetBlackLists.get().contains(BlockReplaceUtils.INSTANCE.getScheState(pos).getBlock())) {
            return true;
        }
        for (PosInfo info : blackList) {
            if (info.pos().equals(pos)) {
                return true;
            }
        }
        return false;
    }


    public void doPrint() {
        if (!timer.passed(iSetPrintingDelay.get())) return;
        if (mc.player == null || mc.world == null) return;
        //刷掉过时的黑名单方块
        blackList.removeIf(b -> System.currentTimeMillis() - b.timestamp() > dSetAntiReplaceTime.get());
        //WorldSchematic worldSchematic = SchematicWorldHandler.getSchematicWorld();

        List<BlockPos> sphere = BlockUtil.getSphere(mc.player.getBlockPos(), dSetPrintingRange.get().intValue(), dSetPrintingRange.get().intValue());
        List<BlockPos> collect = sphere.stream()
            //.filter(SeijaUtil::canPlaceIn)
            .filter(bp -> DataManager.getRenderLayerRange().isPositionWithinRange(bp))
            //投影中可见的方块
            .filter(bp -> !isInBlackList(bp))
            //不在黑名单
            .filter(bp -> BlockUtil.isValidState(BlockReplaceUtils.INSTANCE.getScheState(bp), bp))
            //不是床头之类的不可放置方块
            .filter(bp -> !SeijaUtil.intersectsWithEntity(new Box(bp), entity -> !entity.isSpectator() && !(entity instanceof ItemEntity) && !(entity instanceof ArmorStandEntity)))
            //没被实体卡住
            .filter(bp -> SurfaceUtil.surfaceCheck(bp, iSetSurfaceSize.get()))
            //表面模式检测
            .collect(Collectors.toList());
        PosSorter.sort(collect);
        //放置计数
        int placeCount = 0;
        for (BlockPos blockPos : collect) {//遍历所有的可操作方块
            if (placeCount >= iSetBlockPreTick.get()) return;
            BlockState needState = BlockReplaceUtils.INSTANCE.getScheState(blockPos);//获取需要的方块状态
            BlockState placeNeedState = BlockReplaceUtils.INSTANCE.normalReplaceState(needState);
            PlaceDataPack placeDataPack = PlaceDataManager.getPlaceData(blockPos, placeNeedState);//获取放置数据
            if (placeDataPack.data().valid()) {//如果数据可用
                if (!InvUtil.switchBlock(placeNeedState.getBlock())) {
                    timer.reset();
                    return;
                }
                if (placeDataPack.placeMode()) {
                    BlockUtil.placeBlock(placeDataPack.data());//放置
                } else {
                    BlockUtil.interactBlock(placeDataPack.data());
                }
                timer.reset();//重置计时器
                RenderUtil.isAniRenderSizeAdd = true;
                placeCount += 1;
            }

            //若方块放置失败则尝试修复
            switch (FixerManager.INSTANCE.doFix(blockPos, needState)) {
                case AbstractFixer.SUCCESS: {
                    timer.reset();//若进行了修复操作则重置计时器
                    RenderUtil.isAniRenderSizeAdd = true;
                    placeCount += 1;
                    break;
                }
                case AbstractFixer.RETURN: {
                    timer.reset();
                    return;
                }
                case AbstractFixer.CONTINUE:
            }
        }
        RenderUtil.isAniRenderSizeAdd = false;
    }

    @Override
    public void render3d(Render3DEvent event) {
        if (event == null || mc == null || mc.world == null || mc.player == null) return;
        doPrint();
        RenderUtil.render(event);
    }
//    @EventHandler
//    public void onRender2d(Render2DEvent event){
//        if (aniRenderSize<=30||renderMode.get()!=RenderMode.ANIMATION)return;
//        Vector3d vec3 = new Vector3d(aniRenderCenter.x,aniRenderCenter.y,aniRenderCenter.z);
//        if (NametagUtils.to2D(vec3, 2)) {
//            NametagUtils.begin(vec3);
//            TextRenderer textRenderer = TextRenderer.get();
//            textRenderer.begin(1, false, true);
//
//            String text = mc.player.getMainHandStack().getItem().getName().getString();//String.format("%.1f", renderDamage);
//            double w = textRenderer.getWidth(text) / 2;
//            //RenderUtils.drawItem(event.drawContext, mc.player.getMainHandStack(), (int)textRenderer.getWidth(""), (int)-textRenderer.getHeight() ,3, true);
//
//            textRenderer.render(text, -w, 0, Color.BLACK, true);
//
//            textRenderer.end();
//            NametagUtils.end();
//        }
//
//    }

    @Override
    public void tick(TickEvent.Post e) {
        if (e == null || mc == null || mc.world == null || mc.player == null) return;
        RenderHelper.COLOR.setSpeed(dSetRainbowSpeed.get() / 100);
        RenderHelper.COLOR.getNext();
    }

    @Override
    public void onActivate() {
        super.onActivate();
        FakePlacementContext.updatePlayerEntity();
        //更新玩家实体(避免重连客户端导致原版计算失效)
    }

}
