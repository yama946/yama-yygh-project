package com.yama.test;

import com.yama.yygh.user.ServiceUserApplication;
import com.yama.yygh.user.util.ConstantUserPropertiesUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ServiceUserApplication.class)
public class GetProperties {

    @Test
    public void getProperties(){
        System.out.println(ConstantUserPropertiesUtil.WX_OPEN_APP_SECRET);
        System.err.println(ConstantUserPropertiesUtil.WX_OPEN_APP_ID);
    }
}
