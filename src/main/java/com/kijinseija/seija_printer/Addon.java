package com.kijinseija.seija_printer;


import com.kijinseija.seija_printer.loader.DiskClassLoader;
import com.kijinseija.seija_printer.print_main.hwid.YanZhen;
import com.kijinseija.seija_printer.print_main.modules.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("printer", new ItemStack(Items.BLUE_BANNER));

	@Override
	public void onInitialize() {
		LOG.info("Initializing Seija litematica printer");
        //new YanZhen().yanZheng();

		// Modules
        DiskClassLoader cl = new DiskClassLoader("D:\\test");
        try {
            Class<?> aClass = cl.loadClass("com.kijinseija.seija_printer.loader.InitClass");
            Constructor<?> constructor = aClass.getConstructor();
            Object o = constructor.newInstance(aClass);
            Method method = aClass.getMethod("initModules");
            method.invoke(o);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

//		Modules.get().add(Printer.getINSTANCE());
//        Modules.get().add(new PlaceDebug());
//        Modules.get().add(new ScheDebug());
//        Modules.get().add(new RayTraceTest());
//        Modules.get().add(new YanZhen());
//        Modules.get().add(new SideTest());
//        Modules.get().add(new SwapTest());
	}

    @Override
    public String getPackage() {
        return "com.kijinseija.seija_printer";
    }

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}
