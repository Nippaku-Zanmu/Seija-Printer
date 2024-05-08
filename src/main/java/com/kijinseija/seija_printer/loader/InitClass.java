package com.kijinseija.seija_printer.loader;

import com.kijinseija.seija_printer.print_main.hwid.YanZhen;
import com.kijinseija.seija_printer.print_main.modules.*;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class InitClass {
    public void initModules(){
        Modules.get().add(Printer.getINSTANCE());
        Modules.get().add(new PlaceDebug());
        Modules.get().add(new ScheDebug());
        Modules.get().add(new RayTraceTest());
        Modules.get().add(new YanZhen());
        Modules.get().add(new SideTest());
        Modules.get().add(new SwapTest());
    }
}
