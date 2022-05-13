package com.yama.yygh.msm.service;

import com.yama.yygh.vo.msm.MsmVo;

public interface MsmService {
    //发送手机验证码
    boolean send(String phone, String code);

    //消息发送
    boolean send(MsmVo msmVo);
}
