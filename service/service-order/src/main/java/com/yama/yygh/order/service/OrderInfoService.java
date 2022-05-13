package com.yama.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yama.yygh.model.order.OrderInfo;
import com.yama.yygh.vo.order.OrderQueryVo;

import java.util.Map;

public interface OrderInfoService extends IService<OrderInfo> {
    /**
     * 保存订单信息，并返回订单编号信息
     * @param scheduleId
     * @param patientId
     * @return
     */
    Long saveOrder(String scheduleId, Long patientId);

    //订单列表（条件查询带分页）
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    OrderInfo getOrder(String orderId);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    Map<String,Object> show(Long orderId);

}
