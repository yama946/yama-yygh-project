package com.yama.yygh.hosp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yama.yygh.model.hosp.HospitalSet;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Mapper标记该类是一个mybatis的mapper接口，可以被spring boot自动扫描到spring上下文中
 */
@Mapper
@Repository//需要添加否则，无法作为bean注入到其他类中
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {
}
