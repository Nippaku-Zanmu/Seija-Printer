package com.kijinseija.seija_printer.printer;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.printer.block_fixer.FixerManager;
import com.kijinseija.seija_printer.printer.placedata_getter.PlaceDataManager;
import com.kijinseija.seija_printer.printer.util.*;
import com.kijinseija.seija_printer.settings.DirectionListSetting;
import fi.dy.masa.litematica.data.DataManager;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
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


public class Printer extends Module {
    public static Printer getINSTANCE() {
        return INSTANCE;
    }

    public static final Printer INSTANCE = new Printer();


    private final SettingGroup sgBasicCalc = settings.createGroup("sgBasicCalc");
    public final Setting<Double> printingRange = sgBasicCalc.add(new DoubleSetting.Builder()
        .name("PrintingRange")
        .description("The block place range.")
        .defaultValue(4.7)
        .min(0).sliderMin(0)
        .sliderMax(6)
        .build()
    );
    public final Setting<Double> printingYDistance = sgBasicCalc.add(new DoubleSetting.Builder()
        .name("PrintingYDistance")
        .description("Maximum depth.")
        .defaultValue(2.5)
        .min(0).sliderMin(0)
        .sliderMax(6)
        .build()
    );
    public final Setting<Double> antiReplaceTime = sgBasicCalc.add(new DoubleSetting.Builder()
        .name("AntiReplaceTime")
        .description("")
        .defaultValue(150)
        .min(0).sliderMin(0)
        .sliderMax(1000)
        .build()
    );
    private final Setting<Integer> printingDelay = sgBasicCalc.add(new IntSetting.Builder()
        .name("PrintingDelay")
        .description("Delay between printing blocks in ticks.")
        .defaultValue(51)
        .min(0).sliderMin(0)
        .max(10000).sliderMax(1000)
        .build()
    );

    public final Setting<Boolean> strictDir = sgBasicCalc.add(new BoolSetting.Builder()
        .name("Strict Direction")
        .description("Doesn't place on faces which aren't in your direction.")
        .defaultValue(true)
        .build());

    public final Setting<Integer> predTick = sgBasicCalc.add(new IntSetting.Builder()
        .name("RotatePredTick")
        .defaultValue(1)
        .min(0).sliderMin(0)
        .max(10000).sliderMax(16)
        .build()
    );

    public final Setting<Boolean> sneak = sgBasicCalc.add(new BoolSetting.Builder()
        .name("SneakPlace")
        .defaultValue(true)
        .build());

    public final Setting<Boolean> packetRotate = sgBasicCalc.add(new BoolSetting.Builder()
        .name("PacketRotate")
        .defaultValue(false)
        .build());
    public final Setting<Boolean> packetPlace = sgBasicCalc.add(new BoolSetting.Builder()
        .name("PacketPlace")
        .defaultValue(false)
        .build());
    SettingGroup sgSort = settings.createGroup("Sort");

    public enum DistanceMode {
        LOW, HIGH, NONE
    }

    public final Setting<DistanceMode> angleSortMode = sgSort.add(new EnumSetting.Builder<DistanceMode>()
        .name("angleMode").defaultValue(DistanceMode.LOW).build());
    public final Setting<DistanceMode> distanceSortMode = sgSort.add(new EnumSetting.Builder<DistanceMode>()
        .name("DistanceMode").defaultValue(DistanceMode.HIGH).build());


    SettingGroup sgAdvancedSettings = settings.createGroup("AdvancedSettings");

    public final Setting<Boolean> airPlace = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("Air-Place")
        .description("Allow the bot to place in the air.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> liquidInt = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("LiquidInteract")
        .description("Allow the printer to place on the Liquid.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> surfaceSize = sgAdvancedSettings.add(new IntSetting.Builder()
        .name("SurfaceSize")
        .defaultValue(0)
        .min(0).sliderMin(0)
        .max(10).sliderMax(2)
        .build()
    );
    public final Setting<Boolean> bridgeMode = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("BridgeMode")
        .defaultValue(false)
        .build());
    public final Setting<List<Block>> bridgeBlocks = sgAdvancedSettings.add(new BlockListSetting.Builder()
        .name("BridgeBlocks")
        .visible(bridgeMode::get)
        .defaultValue(Blocks.SLIME_BLOCK)
        .build()
    );
    public final Setting<List<Direction>> bridgeDirs = sgAdvancedSettings.add(new DirectionListSetting.Builder()
        .name("BridgeDirection")
        .visible(bridgeMode::get)
        .defaultValue(Direction.UP)
        .build());


    private final Setting<List<Block>> blackLists = sgAdvancedSettings.add(new BlockListSetting.Builder()
        .name("BlackList")
        .description("Black List.")
        .build()
    );

    public final Setting<Boolean> enablePrecisionPlace = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("enablePrecisionPlace")
        .defaultValue(true)
        .build());
    public final Setting<Boolean> enableBlockFixer = sgAdvancedSettings.add(new BoolSetting.Builder()
        .name("enableBlockFixer")
        .defaultValue(true)
        .build());


    private final SettingGroup sgRendering = settings.createGroup("Rendering");
    public enum RenderMode {
        NONE, MULTI, ANIMATION
    }

    private final Setting<RenderMode> renderMode = sgRendering.add(new EnumSetting.Builder<RenderMode>()
        .name("RenderMode").defaultValue(RenderMode.MULTI).build());
    public final Setting<Double> renderTime = sgRendering.add(new DoubleSetting.Builder()
        .name("renderTime")
        .visible(() -> renderMode.get() == RenderMode.MULTI)
        .defaultValue(500)
        .min(0).sliderMin(0)
        .sliderMax(2000)
        .build()
    );
    public final Setting<Double> animationSpeed = sgRendering.add(new DoubleSetting.Builder()
        .name("AnimationSpeed")
        .visible(() -> renderMode.get() == RenderMode.ANIMATION)
        .sliderRange(0, 100)
        .range(0, 100)
        .defaultValue(10)
        .build());
    public final Setting<Double> sizeExpandMultiplier = sgRendering.add(new DoubleSetting.Builder()
        .name("SizeExpandMultiplier")
        .visible(() -> renderMode.get() == RenderMode.ANIMATION)
        .sliderRange(0, 10)
        .range(0, 100)
        .defaultValue(1.5)
        .build());
    public final Setting<Double> sizeShrinkMultiplier = sgRendering.add(new DoubleSetting.Builder()
        .name("SizeShrinkMultiplier")
        .visible(() -> renderMode.get() == RenderMode.ANIMATION)
        .sliderRange(0, 10)
        .range(0, 100)
        .defaultValue(1)
        .build());

    private final Setting<Double> rainbowSpeed = sgRendering.add(new DoubleSetting.Builder()
        .name("rainbowSpeed")
        .sliderRange(0, 20)
        .defaultValue(1)
        .min(0)
        .visible(() -> renderMode.get() == RenderMode.MULTI)
        .build());
    private final Setting<Boolean> renderFill = sgRendering.add(new BoolSetting.Builder()
        .name("render-fill")
        .defaultValue(true)
        .visible(() -> renderMode.get() != RenderMode.NONE)
        .build()
    );
    private final Setting<SettingColor> fillColor = sgRendering.add(new ColorSetting.Builder()
        .name("colour")
        .description("The cubes colour.")
        .defaultValue(new SettingColor(95, 190, 100))
        .visible(renderFill::isVisible)
        .build()
    );
    private final Setting<Boolean> renderOutline = sgRendering.add(new BoolSetting.Builder()
        .name("outline")
        .defaultValue(true)
        .visible(() -> renderMode.get() != RenderMode.NONE)
        .build()
    );
    private final Setting<SettingColor> outLineColor = sgRendering.add(new ColorSetting.Builder()
        .name("outLine")
        .description("The cubes outline colour.")
        .defaultValue(new SettingColor(95, 190, 255))
        .visible(renderOutline::isVisible)
        .build()
    );
    private final SettingGroup sgReplaceBlockFile = settings.createGroup("ReplaceBlock");
    private final Setting<String> replaceBlockFile =
        sgReplaceBlockFile.add(new StringSetting.Builder()
            .name("replaceBlockFile")
            .defaultValue("D://a.txt")
            .build());


    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        WButton start = list.add(theme.button("Load!")).expandX().widget();
        start.action = () -> new Thread(() -> loadMap(replaceBlockFile.get())).start();

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


    private Printer() {
        super(Addon.CATEGORY, "litematica-printer", "Automatically prints open schematics");
    }

    SeijaTimer timer = new SeijaTimer();
    public final List<PosInfo> blackList = Collections.synchronizedList(new ArrayList<>());

    private boolean isInBlackList(BlockPos pos) {
        if (blackLists.get().contains(BlockReplaceUtils.INSTANCE.getScheState(pos).getBlock())) {
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
        if (!timer.passed(printingDelay.get())) return;
        if (mc.player == null || mc.world == null) return;
        //刷掉过时的黑名单方块
        blackList.removeIf(b -> System.currentTimeMillis() - b.timestamp() > antiReplaceTime.get());
        //WorldSchematic worldSchematic = SchematicWorldHandler.getSchematicWorld();

        List<BlockPos> sphere = BlockUtil.getSphere(mc.player.getBlockPos(), printingRange.get().intValue(), printingRange.get().intValue());
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
            .filter(bp -> BlockUtil.surfaceCheck(bp, surfaceSize.get()))

            .collect(Collectors.toList());
        PosSorter.sort(collect);

        for (BlockPos blockPos : collect) {//遍历所有的可操作方块
            BlockState needState = BlockReplaceUtils.INSTANCE.getScheState(blockPos);//获取需要的方块状态
            BlockState placeNeedState = BlockReplaceUtils.INSTANCE.normalReplaceState(needState);
            PlaceData placeData = getPlaceData(blockPos, placeNeedState);//获取放置数据
            if (placeData.valid()) {//如果数据可用
                InvUtil.switchBlock(placeNeedState.getBlock());//把需要的方块拿到手上
                BlockUtil.placeBlock(placeData);//放置
                timer.reset();//重置计时器
                isAniRenderSizeAdd = true;
                return;
            }

            //若方块放置失败则尝试修复
            if (FixerManager.INSTANCE.doFix(blockPos, needState)) {
                timer.reset();//若进行了修复操作则重置计时器
                isAniRenderSizeAdd = true;
                return;
            }
        }
        isAniRenderSizeAdd = false;
    }


    public PlaceData getPlaceData(BlockPos pos, BlockState needState) {
        List<Direction> dirs = BlockUtil.getDirs(pos);
        if (dirs.isEmpty() || !InvUtil.findBlock(needState.getBlock()) || !BlockUtil.canPlaceIn(pos)) {
            //没有可用Facing//找不到方块//不可放置
            return new PlaceData(null, null, null, false, null);
        }
        return PlaceDataManager.INSTANCE.getPlaceData(pos, needState, dirs);
    }


    public final List<PosInfo> renderList = Collections.synchronizedList(new ArrayList<>());

    public void updateAniRenderSize(double i) {
        aniRenderSize += i;
        aniRenderSize = MathHelper.clamp(aniRenderSize, 0, 100);
    }
    boolean isAniRenderSizeAdd = false;
    Vec3d aniRenderCenter = Vec3d.ZERO;
    double aniRenderSize = 0;

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        doPrint();
        updateRenderList();
        if (renderMode.get() == RenderMode.ANIMATION && !renderList.isEmpty()&&aniRenderSize!=0) {
            //更新显示中点
            Vec3d placeCenter = renderList.get(renderList.size() - 1).pos().toCenterPos();
            double distance = aniRenderCenter.distanceTo(placeCenter);
            if (distance > 16 || distance < 0.1) {
                aniRenderCenter = placeCenter;
            } else {
                Vec3d distanceVec = placeCenter.subtract(aniRenderCenter);
                aniRenderCenter = aniRenderCenter.add(distanceVec.multiply(animationSpeed.get() / 100));
            }
            //渲染
            Vec3d posH1 = aniRenderCenter.add(aniRenderSize / 200, aniRenderSize / 200, aniRenderSize / 200);
            Vec3d posH2 = aniRenderCenter.subtract(aniRenderSize / 200, aniRenderSize / 200, aniRenderSize / 200);
            if (renderFill.get())
                event.renderer.box(new Box(posH1, posH2), fillColor.get(), null, ShapeMode.Sides, 0);
            if (renderOutline.get())
                RenderHelper.drawBoxOutline(new Box(posH1, posH2), outLineColor.get(), event);

        } else if (renderMode.get() == RenderMode.MULTI) {
            renderList.forEach(bi -> {
                BlockPos pos = bi.pos();
                double per = 1 - (System.currentTimeMillis() - bi.timestamp()) / renderTime.get();
                Vec3d posH1 = pos.toCenterPos().add(per * 0.5, per * 0.5, per * 0.5);
                Vec3d posH2 = pos.toCenterPos().subtract(per * 0.5, per * 0.5, per * 0.5);

                if (renderFill.get()) {
                    Color col = fillColor.get().rainbow ? bi.renderColor().a(fillColor.get().a) : new Color(fillColor.get().r, fillColor.get().g, fillColor.get().b, (int) (per * fillColor.get().a));
                    event.renderer.box(new Box(posH1, posH2), col, null, ShapeMode.Sides, 0);
                }
                if (renderOutline.get()) {
                    Color col = outLineColor.get().rainbow ? bi.renderColor().a(outLineColor.get().a) : new Color(outLineColor.get().r, outLineColor.get().g, outLineColor.get().b, (int) (per * outLineColor.get().a));
                    RenderHelper.drawBoxOutline(/*pos,*/ new Box(posH1, posH2), col, event);
                }
            });
        }
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

    @EventHandler()
    private void tick(TickEvent.Post e) {
        RenderHelper.COLOR.setSpeed(rainbowSpeed.get() / 100);
        RenderHelper.COLOR.getNext();

    }
    private void updateRenderList(){
        if (renderMode.get() != RenderMode.MULTI && !renderList.isEmpty()) {
            PosInfo posInfo = renderList.get(renderList.size() - 1);
            renderList.clear();
            renderList.add(posInfo);
            if (isAniRenderSizeAdd)
                updateAniRenderSize(sizeExpandMultiplier.get());
            else
                updateAniRenderSize(-sizeShrinkMultiplier.get());
        }
        renderList.removeIf(b -> System.currentTimeMillis() - b.timestamp() > renderTime.get());

    }

}
