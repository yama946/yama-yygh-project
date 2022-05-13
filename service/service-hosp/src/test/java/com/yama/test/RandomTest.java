package com.yama.test;

import com.yama.yygh.hosp.ServiceHospApplication;
import com.yama.yygh.hosp.mapper.HospitalSetMapper;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;


@SpringBootTest(classes = ServiceHospApplication.class)
public class RandomTest {
    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    /**
     * 测试Random对象的使用
     */
    @Test
    public void randomUse(){
        Random random = new Random();
        //用来返回一个0-1000之间的值
        for (int i = 0; i < 200; i++) {
            int anInt = random.nextInt(1000);
            System.out.println(anInt);
        }
    }
    @Test
    public void randomMath(){
        double random = Math.random();
        System.out.println(random);
    }
}
