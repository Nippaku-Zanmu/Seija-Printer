package com.kijinseija.seija_printer.loader;

import com.kijinseija.seija_printer.Addon;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class DiskClassLoader extends ClassLoader {
    private final HashMap<String, byte[]> classMap = new HashMap<>();
    private String mLibPath;


    public final void downloadClass() {

        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 7766);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            bw.write(new YanZhen().getHWID());
            bw.newLine();
            bw.flush();
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(socket.getInputStream()));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len = 0;

                while ((len = zis.read()) != -1) {
                    bos.write(len);
                }
                Addon.LOG.info("Download"+entry.getName());
                byte[] classByte = bos.toByteArray();//new byte[(int) entry.getSize()];
//                zis.read(classByte);

                classMap.put(entry.getName(), classByte);
            }
            Addon.LOG.info("Downloaded");

        } catch (IOException e) {
            //throw new RuntimeException(e);
        } finally {
            try {
                Thread.sleep(500);
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException | InterruptedException e) {
              //  throw new RuntimeException(e);
            }
            Addon.LOG.info("Downloaded1");
        }
    }


    public DiskClassLoader(String path) {
        super(Modules.get().getClass().getClassLoader());
        //使用依赖mod的classLoader 避免加载类的问题
        // TODO Auto-generated constructor stub
        mLibPath = path;
    }

//    @Override
//    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        try {
//            return super.loadClass(name, resolve);
//        }catch (ClassNotFoundException)
//
//    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Addon.LOG.info("Try Load Class 1111   " + name);
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) {
        // TODO Auto-generated method stub

        String fileName = getFileName(name);
        Addon.LOG.info("Loading "+name);
        //File file = new File(mLibPath, fileName);

        //  try {

//            FileInputStream is = new FileInputStream(file);
//
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            int len = 0;
//            try {
//                while ((len = is.read()) != -1) {
//                    bos.write(len);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
        byte[] data = classMap.get(fileName);
        if (data == null) {
            Addon.LOG.info(fileName + "   Is Null");
            classMap.forEach((s, bytes) -> Addon.LOG.info("Class: " + s));
        }
        //bos.toByteArray();
        //System.out.println(Arrays.toString(data));
//            is.close();
//            bos.close();

        return defineClass(name, data, 0, data.length);

        //} catch (IOException e) {
        // TODO Auto-generated catch block
        //    e.printStackTrace();
        // }

        //  return super.findClass(name);
    }

    //获取要加载 的class文件名
    private String getFileName(String name) {
        return name.replaceAll("\\.", "/") + ".class";
        // TODO Auto-generated method stub
//		int index = name.lastIndexOf('.');
//		if(index == -1){
//			System.out.println(name+".class");
//			return name+".class";
//		}else{
//			String s = name.substring(index + 1) + ".class";
////			System.out.println(222);
////			System.out.println(name.substring(index + 1));
////			System.out.println(s);
//			return s;
//		}
    }

}
