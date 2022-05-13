package com.yama.test;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

/**
 * 测试String工具类
 */
public class StringUtilsTest {

    @Test
    public void stringUtilsTest(){
        String str1 ="";
        boolean empty = StringUtils.isEmpty(str1);
        System.out.println(empty);
    }
}
