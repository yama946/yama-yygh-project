package com.yama.yygh.service.helper;

import com.yama.yygh.common.util.MD5;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class HttpRequestHelper {
    /**
     * 将请求的map集合中，String[]数组转换为object
     * @param paramMap
     * @return
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap){
        Map<String, Object> switchMap = new HashMap<>();
        //从map中获取键值对象，用于json转换
        for (Map.Entry<String,String[]> entryMap : paramMap.entrySet()){
            switchMap.put(entryMap.getKey(),entryMap.getValue()[0]);
        }
        log.info("请求参数集合转换成功");
        return switchMap;
    }

    /**
     * 使用数据库中获取的signkey进行md5加密，与传输俩的sign进行比较
     *
     * TreeMap中所有的元素都是有某一固定顺序的，如果需要得到一个有序的结果，就应该使用TreeMap；
     * TreeMap和linkedHashMap简单比较
     * TreeMap集合数据结构是红黑树，linkedHashMap是链表
     * linkeHashMap也是可以有序的存取数据，但是效率没有TreeMap要快
     * @param paramMap
     * @param signKey
     * @return
     */
    public static String getSign(Map<String, Object> paramMap, String signKey) {
        //1、判断map中是否存在sign键值，并排除
        if (paramMap.containsKey("sign")) paramMap.remove("sign");
        //2、将排除后的map集合，放到一个新的map集合中，进行顺序存取
        TreeMap<String, Object> treeMap = new TreeMap<>(paramMap);
        StringBuilder stringBuilder = new StringBuilder();
        //3、通过Stringbuile进行追加成字符串
        for (Map.Entry<String,Object> mapEntry : treeMap.entrySet()){
            stringBuilder.append(mapEntry.getValue()).append("|");
        }
        stringBuilder.append(signKey);
        //4、将追加后的字符串进行MD5编码加密
        log.info("加密前："+stringBuilder.toString());
        String md5Str = MD5.encrypt(stringBuilder.toString());
        log.info("加密后："+md5Str);
        return md5Str;
    }

    /**
     * 签名校验
     * @param paramMap
     * @param signKey
     * @return
     */
    public static boolean isSignEquals(Map<String, Object> paramMap, String signKey) {
        String sign = (String)paramMap.get("sign");
        String md5Str = getSign(paramMap, signKey);
        if(Objects.equals(sign,signKey)) {
            return false;
        }
        return true;
    }

    /**
     * 获取时间戳
     * @return
     */
    public static long getTimestamp() {
        return new Date().getTime();
    }

}
