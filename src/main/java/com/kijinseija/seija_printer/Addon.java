package com.kijinseija.seija_printer;


import com.kijinseija.seija_printer.loader.DiskClassLoader;
import com.kijinseija.seija_printer.print_main.InitClass;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.BlockStateVerify;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL;

import java.lang.reflect.Constructor;

import static org.lwjgl.opengl.GL11C.GL_DONT_CARE;
import static org.lwjgl.opengl.GL43.glDebugMessageControl;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("printer", new ItemStack(Items.BLUE_BANNER));

	@Override
	public void onInitialize() {


        LOG.info("Initializing Seija litematica printer");
        GL.createCapabilities();

        // 禁用OpenGL调试消息
        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, (int[]) null, false);
        MeteorClient.EVENT_BUS.subscribe(BlockStateVerify.class);
		// Modules
//        DiskClassLoader cl = new DiskClassLoader();
//        cl.downloadClass();
//        try {
//            LOG.info("Try Load Class1");
//            Class aClass = cl.loadClass("com.kijinseija.seija_printer.print_main.InitClass");
//            LOG.info("Try Load Class2");
//
//            Constructor<?> constructor = aClass.getConstructor();
//            LOG.info("Try Load Class3");
//            Object o = constructor.newInstance();
//        } catch (Exception e) {
//            LOG.error(e.getMessage());
//            throw new RuntimeException(e);
//        }
        new InitClass();
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
