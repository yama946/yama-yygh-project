package com.yama.yygh.hosp.service;

import com.yama.yygh.model.hosp.Schedule;
import com.yama.yygh.vo.hosp.ScheduleOrderVo;
import com.yama.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 排班信息管理的服务层
 */
public interface ScheduleService {
    /**
     * 上传排班信息,保存到mongo中
     * @param paramMap
     */
    void save(Map<String, Object> paramMap);

    /**
     * 分页查询
     * @param page 当前页码
     * @param limit 每页记录数
     * @param scheduleQueryVo 查询条件
     * @return
     */
    Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    /**
     * 删除排班信息
     * @param hoscode
     * @param hosScheduleId
     */
    void remove(String hoscode, String hosScheduleId);

    /**
     * 根据医院编号 和 科室编号 ，查询排班规则数据
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);
    //-------------------------------用户前台服务------------------------------------------

    /**
     *获取排班可预约日期数据
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    /**
     * 根据id获取排班
     * @param id
     * @return
     */
    Schedule getById(String id);

    //----------------------------------远程调用方法--------------------------------------
    /**
     * 获取订单排班信息
     * @param scheduleId
     * @return
     */
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);
    //-----------------------------rabbitmq消息监听----------------------------
    /**
     * 更新排班信息
     * @param schedule
     */
    void update(Schedule schedule);
}
