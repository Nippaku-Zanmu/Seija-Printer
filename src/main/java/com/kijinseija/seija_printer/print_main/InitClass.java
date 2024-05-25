package com.kijinseija.seija_printer.print_main;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.print_main.modules.*;
import meteordevelopment.meteorclient.systems.modules.Modules;

import java.util.logging.Logger;

public class InitClass {
    public InitClass() {
//        //Addon.LOG.info("loader");
//        try {
//
        Modules.get().add(new PlaceDebug());
        Modules.get().add(new ScheDebug());
        Printer pri = new Printer();
        pri.onRender3d(null);
        pri.tick(null);
        Modules.get().add(pri);
        Modules.get().add(new RayTraceTest());
//            Modules.get().add(new YanZhen());
        Modules.get().add(new SideTest());
        Modules.get().add(new SwapTest());

//        }catch (Exception e){
//           // Addon.LOG.info(e.getMessage());
//        }
    }
}
