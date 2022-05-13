package com.yama.yygh.hosp.repository;

import com.yama.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 医院上传接口
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    /**
     * 通过医院编号查询
     * @param hoscode
     * @return
     */
    Hospital findHospitalByHoscode(String hoscode);

    /**
     * 根据名称模糊查询对应医院信息
     * @param hosname
     * @return
     */
    List<Hospital> findHospitalByHosnameLike(String hosname);
}
