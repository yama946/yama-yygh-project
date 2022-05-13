package com.yama.esayExcel.writeandread;

import com.alibaba.excel.EasyExcel;
import com.yama.esayExcel.TestApplication;
import com.yama.esayExcel.entity.DemoData;
import com.yama.esayExcel.listenner.DemoDataListener;
import com.yama.esayExcel.service.DemoDataService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;


//@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class ReadTest {

    @Autowired
    private DemoDataService demoDataService;

    @Test
    public void simpleRead(){
        String path = "d:";
        String fileName = path + File.separator + "demo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new DemoDataListener(demoDataService)).sheet().doRead();
    }
}
