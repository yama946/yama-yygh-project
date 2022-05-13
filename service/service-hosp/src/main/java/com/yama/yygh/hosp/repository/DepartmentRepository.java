package com.yama.yygh.hosp.repository;

import com.yama.yygh.model.hosp.Department;
import com.yama.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *上传科室信息
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {
    /**
     * 根据医院编号和科室编号查询科室信息
     * @param hoscode
     * @param depcode
     * @return
     */
    Department findDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    /**
     * 根据科室编号进行查询分页，这里分页方法我们未使用，只是测试告知我们可以自定义这样的方法
     * 我们使用findAll提供的分页方法，进行分页操作
     * @param depName
     * @param pageable
     * @return
     */
    Page<Department> findPageByDepname(String depName, Pageable pageable);

}
