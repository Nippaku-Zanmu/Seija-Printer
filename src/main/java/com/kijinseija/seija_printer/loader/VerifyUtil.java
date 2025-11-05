/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.loader;

import com.kijinseija.seija_printer.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class VerifyUtil extends Module implements Serializable {
     int k ;
    public VerifyUtil() {
        super(Addon.CATEGORY,"Hello Meteor","");
    }

    private  SecretKey generateKey(byte[] key) throws Exception {
        // 根据指定的 RNG 算法, 创建安全随机数生成器
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        // 设置 密钥key的字节数组 作为安全随机数生成器的种子
        random.setSeed(key);

        // 创建 AES算法生成器
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        // 初始化算法生成器
        gen.init(128, random);

        // 生成 AES密钥对象, 也可以直接创建密钥对象: return new SecretKeySpec(key, ALGORITHM);
        return gen.generateKey();
    }

    public String encrypt(String minWen, String key) throws Exception {
        byte[] min = minWen.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return encrypt(min, keyBytes);

    }

    /**
     * 数据加密: 明文 -> 密文
     */
    public  String encrypt(byte[] plainBytes, byte[] key) throws Exception {
        // 生成密钥对象
        SecretKey secKey = generateKey(key);

        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化密码器（加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, secKey);

        byte[] b = cipher.doFinal(plainBytes);
        // 加密数据, 返回密文

        //Base64编码,便于字符流存储
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(b);
//        BASE64Encoder be = new BASE64Encoder();
//        return be.encode(b);
    }

    public  String decrypt(String miwen, String key) throws Exception {
//        byte[] miwenBytes = miwen.getBytes(StandardCharsets.UTF_8);
//        if (miwenBytes.length % 16 != 0) {
////            byte[] bytes = new byte[16*((miwenBytes.length/16)+1)];
////            Arrays.copyOf()
//            miwenBytes = Arrays.copyOf(miwenBytes, miwenBytes.length + (16 - miwenBytes.length % 16));
//        }
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return new String(decrypt(miwen, keyBytes), StandardCharsets.UTF_8);
    }

    /**
     * 数据解密: 密文 -> 明文
     */
    public  byte[] decrypt(String miwen, byte[] key) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] cipherBytes = decoder.decode(miwen);
//        Base64Decoder bd = new Base64Decoder();
//        byte[] cipherBytes = bd.decodeBuffer(miwen);
        //Base64解码
        // 生成密钥对象
        SecretKey secKey = generateKey(key);

        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化密码器（解密模型）
        cipher.init(Cipher.DECRYPT_MODE, secKey);

        // 解密数据, 返回明文
        return cipher.doFinal(cipherBytes);
    }




    public  String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);

            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                + "Set colItems = objWMIService.ExecQuery _ \n"
                + "   (\"Select * from Win32_BaseBoard\") \n"
                + "For Each objItem in colItems \n"
                + "    Wscript.Echo objItem.SerialNumber \n"
                + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 获取硬盘序列号
     *
     * @param drive
     *            盘符
     * @return
     */
    public  String getHardDiskSN(String drive) {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);

            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                + "Set colDrives = objFSO.Drives\n"
                + "Set objDrive = colDrives.item(\""
                + drive
                + "\")\n"
                + "Wscript.Echo objDrive.SerialNumber"; // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 获取CPU序列号
     *
     * @return
     */
    public  String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                + "Set colItems = objWMIService.ExecQuery _ \n"
                + "   (\"Select * from Win32_Processor\") \n"
                + "For Each objItem in colItems \n"
                + "    Wscript.Echo objItem.ProcessorId \n"
                + "    exit for  ' do the first cpu only! \n" + "Next \n";

            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "无CPU_ID被读取";
        }
        return result.trim();
    }

    /**
     * 获取MAC地址
     */
    public  String getMac() {
        String result = "";
        try {

            Process process = Runtime.getRuntime().exec("ipconfig /all");

            InputStreamReader ir = new InputStreamReader(
                process.getInputStream());

            LineNumberReader input = new LineNumberReader(ir);

            String line;

            while ((line = input.readLine()) != null)

                if (line.indexOf("Physical Address") > 0) {

                    String MACAddr = line.substring(line.indexOf("-") - 2);

                    result = MACAddr;

                }

        } catch (IOException e) {

            System.err.println("IOException " + e.getMessage());

        }
        return result;
    }

    final File saveDir = new File("meteor-client");
    final File savePath = new File(saveDir.getPath() + "/printerHWID.txt");

   public String getHWID() {

       try {
           String hwid = encrypt(getCPUSerial() + getMac() + getMotherboardSN(), "Seija");
           Addon.LOG.info("You HWID :"+hwid);
           return hwid;

       } catch (Exception e) {
           System.exit(114514);
           return null;
       }

   }

   public void yanZheng(){
       String s ="";
       try {
           if (!savePath.exists())savePath.createNewFile();
           BufferedReader br = new BufferedReader(new FileReader(savePath));
           s = br.readLine();
           br.close();
           if (s.equals(encrypt(getHWID(),"00oopp"))){
                return;
           }
       } catch (Exception e) {

       }
       Addon.LOG.info("Key Error,You Key:"+s);
       System.exit(114514);
       kill();
   }
   public void kill(){
       int i = 1/0;
       while (true){}
   }
}
