package com.yama.yygh.msm.service.impl;


import com.yama.yygh.msm.service.MsmService;
import com.yama.yygh.vo.msm.MsmVo;
import lombok.extern.slf4j.Slf4j;
import com.yama.yygh.msm.util.MessagePropertiesUtile;
import com.yama.yygh.msm.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class MsmServiceImpl implements MsmService {

    @Autowired
    private MessagePropertiesUtile messagePropertiesUtile;

    /**
     * 发送短信验证码
     * @param phone
     * @param code
     * @return
     */
    @Override
    public boolean send(String phone, String code) {
        if (phone==null){
            return false;
        }
        String host = messagePropertiesUtile.getApiHost();
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = messagePropertiesUtile.getAppCode();
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", "**code**:"+ code +",**minute**:5");
        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            log.debug("服务层发送验证码："+response.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //------------------------------------rabbit监听器方法-----------------------------
    @Override
    public boolean send(MsmVo msmVo) {
        if(!StringUtils.isEmpty(msmVo.getPhone())) {
            String code = (String)msmVo.getParam().get("code");
            return this.send(msmVo.getPhone(),code);
        }
        return false;
    }


}
