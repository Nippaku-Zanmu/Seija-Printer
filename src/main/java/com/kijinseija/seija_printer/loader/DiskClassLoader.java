package com.kijinseija.seija_printer.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;


public class DiskClassLoader extends ClassLoader {

	private String mLibPath;

	public DiskClassLoader(String path) {
		// TODO Auto-generated constructor stub
		mLibPath = path;
	}




	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub

		String fileName = getFileName(name);

		File file = new File(mLibPath,fileName);

		try {

			FileInputStream is = new FileInputStream(file);



			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int len = 0;
	        try {
	            while ((len = is.read()) != -1) {
	            	bos.write(len);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        byte[] data = bos.toByteArray();
			//System.out.println(Arrays.toString(data));
			is.close();
	        bos.close();

	        return defineClass(name,data,0,data.length);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.findClass(name);
	}

	//获取要加载 的class文件名
	private String getFileName(String name) {
		return name.replaceAll("\\.","\\\\")+".class";
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
