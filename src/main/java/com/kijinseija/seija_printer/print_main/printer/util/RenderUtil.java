package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderUtil {
    public static final List<PosInfo> renderList = Collections.synchronizedList(new ArrayList<>());

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Printer pri = Printer.getINSTANCE();

    public static void updateAniRenderSize(double i) {
        aniRenderSize += i;
        aniRenderSize = MathHelper.clamp(aniRenderSize, 0, 100);
    }

    public static boolean isAniRenderSizeAdd = false;
    private static Vec3d aniRenderCenter = Vec3d.ZERO;
    private static double aniRenderSize = 0;

    public static void render(Render3DEvent event) {
        updateRenderList();
        if (pri.eSetRenderMode.get() == Printer.RenderMode.ANIMATION && !renderList.isEmpty() && aniRenderSize != 0) {
            animationRender(event);
        } else if (pri.eSetRenderMode.get() == Printer.RenderMode.MULTI) {
            multiRender(event);
        }else if (pri.eSetRenderMode.get()== Printer.RenderMode.DEBUG){
            debugRender(event);
        }
    }

    private static final void multiRender(Render3DEvent event) {
        renderList.forEach(pi -> {
            BlockPos pos = pi.pos();
            double per = 1 - (System.currentTimeMillis() - pi.timestamp()) / pri.dSetRenderTime.get();
            Vec3d posH1 = pos.toCenterPos().add(per * 0.5, per * 0.5, per * 0.5);
            Vec3d posH2 = pos.toCenterPos().subtract(per * 0.5, per * 0.5, per * 0.5);

            if (pri.bSetRenderFill.get()) {
                Color col = pri.colSetFillColor.get().rainbow ? pi.renderColor().a(pri.colSetFillColor.get().a) : new Color(pri.colSetFillColor.get().r, pri.colSetFillColor.get().g, pri.colSetFillColor.get().b, (int) (per * pri.colSetFillColor.get().a));
                event.renderer.box(new Box(posH1, posH2), col, null, ShapeMode.Sides, 0);
            }
            if (pri.bSetRenderOutline.get()) {
                Color col = pri.colSetOutLineColor.get().rainbow ? pi.renderColor().a(pri.colSetOutLineColor.get().a) : new Color(pri.colSetOutLineColor.get().r, pri.colSetOutLineColor.get().g, pri.colSetOutLineColor.get().b, (int) (per * pri.colSetOutLineColor.get().a));
                RenderHelper.drawBoxOutline(/*pos,*/ new Box(posH1, posH2), col, event);
            }
        });
    }

    private static void debugRender(Render3DEvent event) {
        renderList.forEach(pi -> {
            Direction dir = pi.torchDir();
            Vec3d center = pi.clickVec();
            Vec3d c1 = pi.clickVec();
            Vec3d c2 = pi.clickVec();
            for (Direction.Axis ax : Direction.Axis.values()) {
                if (ax.equals(dir.getAxis())) continue;
                c1 = c1.offset(Direction.from(ax, Direction.AxisDirection.NEGATIVE), 0.15);
                c2 = c2.offset(Direction.from(ax, Direction.AxisDirection.NEGATIVE), -0.15);

            }
            event.renderer.box(new Box(c1,c2),pri.colSetFillColor.get(),pri.colSetOutLineColor.get(),ShapeMode.Both,1);
            Vec3d off = center.offset(dir,0.4);
            Vec3d centerPos = pi.pos().toCenterPos();
            event.renderer.line(center.x,center.y,center.z, off.x, off.y, off.z,pi.isPlaceMode()?Color.WHITE:Color.BLACK);
            event.renderer.box(new Box(centerPos.add(0.05,0.05,0.05),centerPos.add(-0.05,-0.05,-0.05)),Color.ORANGE,pri.colSetOutLineColor.get(),ShapeMode.Both,1);
        });
    }

    private static void animationRender(Render3DEvent event) {
        //更新显示中点
        Vec3d placeCenter = renderList.get(renderList.size() - 1).pos().toCenterPos();
        double distance = aniRenderCenter.distanceTo(placeCenter);
        if (distance > 16 || distance < 0.1) {
            aniRenderCenter = placeCenter;
        } else {
            Vec3d distanceVec = placeCenter.subtract(aniRenderCenter);
            aniRenderCenter = aniRenderCenter.add(distanceVec.multiply(pri.dSetAnimationSpeed.get() / 100));
        }
        //渲染
        Vec3d posH1 = aniRenderCenter.add(aniRenderSize / 200, aniRenderSize / 200, aniRenderSize / 200);
        Vec3d posH2 = aniRenderCenter.subtract(aniRenderSize / 200, aniRenderSize / 200, aniRenderSize / 200);
        if (pri.bSetRenderFill.get())
            event.renderer.box(new Box(posH1, posH2), pri.colSetFillColor.get(), null, ShapeMode.Sides, 0);
        if (pri.bSetRenderOutline.get())
            RenderHelper.drawBoxOutline(new Box(posH1, posH2), pri.colSetOutLineColor.get(), event);

    }

    private static void updateRenderList() {
        if (pri.eSetRenderMode.get() == Printer.RenderMode.ANIMATION && !renderList.isEmpty()) {
            PosInfo posInfo = renderList.get(renderList.size() - 1);
            renderList.clear();
            renderList.add(posInfo);
            if (isAniRenderSizeAdd)
                updateAniRenderSize(pri.dSetSizeExpandMultiplier.get());
            else
                updateAniRenderSize(-pri.dSetSizeShrinkMultiplier.get());
        }
        renderList.removeIf(b -> System.currentTimeMillis() - b.timestamp() > pri.dSetRenderTime.get());

    }
}
