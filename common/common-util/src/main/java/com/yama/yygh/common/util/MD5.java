package com.yama.yygh.common.util;

import com.yama.yygh.common.exception.YyghException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    public static String encrypt(String strSrc) {
        try {
            char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            byte[] bytes = strSrc.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }
    //对明文密码进行MD5进行加密，加密后不可逆，因此下次比较需要加密后比较，为了保护数据安全
    public static String md5(String source){
        //1\判断源字符是否是有效字符，否则抛出异常
        if (source == null || source.length()==0){
            //2\如果不是有效字符，则抛出异常,运行时异常无序处理直接交给jvm处理
            throw new YyghException("当前加密字符串为空，请重新输入");
        }
        try{
            //3\获取MessageDigest对象，为了放置幽灵变量，数据尽量放到变量中
            String algorithm = "MD5";
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            //4\获取明文字符串中对应的字符数组
            byte[] input = source.getBytes();

            //5\对明文数据进行哈希加密
            byte[] digest = messageDigest.digest(input);

            // 6.创建BigInteger 对象
            int signum = 1;
            BigInteger bigInteger = new BigInteger(signum, digest);
            // 7.按照16 进制将bigInteger 的值转换为字符串
            int radix = 16;
            String encoded = bigInteger.toString(radix).toUpperCase();
            return encoded;
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }
}
