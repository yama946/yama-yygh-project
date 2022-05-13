package com.yama.yygh.task.util;

import com.yama.yygh.rabbit.constant.MqConstant;
import com.yama.yygh.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    /**
     * 每天8点执行 提醒就诊
     * cron是一种表达式
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "0/30 * * * * ?")
    public void task1() {
        rabbitService.sendMessage(MqConstant.EXCHANGE_DIRECT_TASK, MqConstant.ROUTING_TASK_8, "");
    }
}

