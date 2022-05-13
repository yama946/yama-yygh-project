package com.yama.yygh.hosp.repository;

import com.yama.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 上传排班信息进行管理
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    /**
     * 根据医院编号和排班编号，获取排班信息
     * @param hoscode
     * @param hosScheduleId
     * @return
     */
    Schedule findScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     * @param hoscode
     * @param depcode
     * @param toDate
     * @return
     */
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);

}
