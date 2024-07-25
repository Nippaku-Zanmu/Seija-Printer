package com.kijinseija.seija_printer;


import com.kijinseija.seija_printer.loader.DiskClassLoader;
import com.kijinseija.seija_printer.print_main.InitClass;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryUtil;

import java.lang.reflect.Constructor;

import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_DONT_CARE;
import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.opengl.GL43.glDebugMessageControl;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT_SYNCHRONOUS;
import static org.lwjgl.opengl.KHRRobustness.GL_NO_ERROR;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("printer", new ItemStack(Items.BLUE_BANNER));
//    public static void checkGLError(String location) {
//        int error;
//        while ((error = glGetError()) != GL_NO_ERROR) {
//            LOG.error("OpenGL Error at {}: {}", location, error);
//        }
//    }
	@Override
	public void onInitialize() {


        LOG.info("Initializing Seija litematica printer");
        GL.createCapabilities();

        // 禁用OpenGL调试消息
        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, (int[]) null, false);
//        glEnable(GL_DEBUG_OUTPUT);
//        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
//        glDebugMessageCallback(new GLDebugMessageCallback() {
//            @Override
//            public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
//                String errorMessage = GLDebugMessageCallback.getMessage(length, message);
//                LOG.error("OpenGL Error: Source={}, Type={}, ID={}, Severity={}, Message={}", source, type, id, severity, errorMessage);
//            }
//        }, MemoryUtil.NULL);

        new InitClass();
		// Modules
//        DiskClassLoader cl = new DiskClassLoader("D:\\test\\seija-printer-1.4\\");
//        cl.downloadClass();
//        try {
//            LOG.info("Try Load Class1");
//            Class aClass = cl.loadClass("com.kijinseija.seija_printer.print_main.InitClass");
//            LOG.info("Try Load Class2");
//
//            Constructor<?> constructor = aClass.getConstructor();
//            LOG.info("Try Load Class3");
//            Object o = constructor.newInstance();
//            //Method method = aClass.getMethod("initModules");
//            //method.invoke(o);
//           // new InitClass().initModules();
//        } catch (Exception e) {
//            LOG.error(e.getMessage());
//            throw new RuntimeException(e);
//        }

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
