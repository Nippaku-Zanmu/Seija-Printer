package com.kijinseija.seija_printer;

import com.kijinseija.seija_printer.print_main.hwid.YanZhen;
import com.kijinseija.seija_printer.print_main.printer.PlaceDebug;
import com.kijinseija.seija_printer.print_main.printer.Printer;
import com.kijinseija.seija_printer.print_main.printer.RayTraceTest;
import com.kijinseija.seija_printer.print_main.printer.ScheDebug;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("printer", new ItemStack(Items.BLUE_BANNER));

	@Override
	public void onInitialize() {
		LOG.info("Initializing Seija litematica printer");
        //new YanZhen().yanZheng();

		// Modules
		Modules.get().add(Printer.getINSTANCE());
        Modules.get().add(new PlaceDebug());
        Modules.get().add(new ScheDebug());
        Modules.get().add(new RayTraceTest());

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
