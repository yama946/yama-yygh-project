package com.yama.yygh.msm.listener;

import com.rabbitmq.client.Channel;
import com.yama.yygh.msm.service.MsmService;
import com.yama.yygh.rabbit.constant.MqConstant;
import com.yama.yygh.vo.msm.MsmVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbit监听器，消费者端，用来处理rabbit队列中的消息
 */
@Component
public class MsmListener {
    @Autowired
    private MsmService msmService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstant.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConstant.EXCHANGE_DIRECT_MSM),
            key = {MqConstant.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) {
        msmService.send(msmVo);
    }
}

