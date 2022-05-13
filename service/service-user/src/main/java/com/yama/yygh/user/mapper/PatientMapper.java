package com.yama.yygh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yama.yygh.model.user.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 实现对就诊人（病人）的管理
 */
@Mapper
@Repository
public interface PatientMapper extends BaseMapper<Patient> {
}
