package com.yama.esayExcel.test;

import com.yama.yygh.cmn.Service.DictService;
import com.yama.yygh.cmn.ServiceCmnApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ServiceCmnApplication.class)
public class DictServiceTest {
    @Autowired
    private DictService dictService;
    /**
     * 测试导出功能
     */
}
