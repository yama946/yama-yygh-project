package com.yama.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yama.yygh.model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {
    /**
     * 根据用户id，查询所有的就诊人
     * @param userId
     * @return
     */
    List<Patient> findAllUserId(Long userId);

    /**
     * 根据就诊人的id获取，就诊人详情
     * @param id
     * @return
     */
    Patient getPatientId(Long id);
}
