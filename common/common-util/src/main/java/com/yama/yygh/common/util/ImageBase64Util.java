package com.yama.yygh.common.util;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 将图片转换位base64编码的工具类
 */
public class ImageBase64Util {

    public static void main(String[] args) {
        String imageFile= "D:\\yygh_work\\xh.png";// 待处理的图片
        System.out.println(getImageString(imageFile));
    }

    public static String getImageString(String imageFile){
        InputStream is = null;
        try {
            byte[] data = null;
            //1、创建输入流
            is = new FileInputStream(new File(imageFile));
            //available方法用来估计流畅读取文件的字节数
            //2、创建缓存字节数组
            data = new byte[is.available()];
            //3、将文件以字节的形式读取到指定字节数组data中
            is.read(data);
            //4、将字节数组进行base64编码转换成字符串
            return new String(Base64.encodeBase64(data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                    is = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}

