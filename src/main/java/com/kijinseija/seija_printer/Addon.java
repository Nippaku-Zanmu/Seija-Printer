package com.kijinseija.seija_printer;


import com.kijinseija.seija_printer.loader.DiskClassLoader;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("printer", new ItemStack(Items.BLUE_BANNER));

	@Override
	public void onInitialize() {
		LOG.info("Initializing Seija litematica printer");
        //new YanZhen().yanZheng();

		// Modules
        DiskClassLoader cl = new DiskClassLoader("D:\\test\\seija-printer-1.4-beta\\");
        cl.downloadClass();
        try {
            LOG.info("Try Load Class1");
            Class aClass = cl.loadClass("com.kijinseija.seija_printer.print_main.InitClass");
            LOG.info("Try Load Class2");

            Constructor<?> constructor = aClass.getConstructor();
            LOG.info("Try Load Class3");
            Object o = constructor.newInstance();
            //Method method = aClass.getMethod("initModules");
            //method.invoke(o);
           // new InitClass().initModules();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

//		Modules.get().add(Printer.getINSTANCE());
//        Modules.get().add(new PlaceDebug());
//        Modules.get().add(new ScheDebug());
//        Modules.get().add(new RayTraceTest());
////        Modules.get().add(new YanZhen());
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
