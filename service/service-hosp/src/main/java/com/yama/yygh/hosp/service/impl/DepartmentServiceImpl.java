package com.yama.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yama.yygh.common.util.BeanUtils;
import com.yama.yygh.hosp.repository.DepartmentRepository;
import com.yama.yygh.hosp.service.DepartmentService;
import com.yama.yygh.model.hosp.Department;
import com.yama.yygh.vo.hosp.DepartmentQueryVo;
import com.yama.yygh.vo.hosp.DepartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 保存上传的科室信息
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Department.class);
        Department targetDepartment = departmentRepository.findDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if (null != targetDepartment) {
            //copy不为null的值，该方法为自定义方法
            BeanUtils.copyBean(department, targetDepartment, Department.class);
            departmentRepository.save(targetDepartment);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    /**
     * 分页查询科室信息进行显示
     * @param page 当前页码
     * @param limit 每页记录数
     * @param departmentQueryVo 查询条件
     * @return
     */
    @Override
    public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        //1、定义查询到的数据排序方式
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //2、获取一个分页参数对象，0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);

        //3、创建匹配器，即如何使用查询条件，jpa动态查询
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //创建实例
        Example<Department> example = Example.of(department, matcher);
        Page<Department> pages = departmentRepository.findAll(example, pageable);
        return pages;
    }

    /**
     * mongo不支持自定义删除方法，我们只能查询到对象，
     * 获取其主键id，通过主键id的形式进行删除对象
     * @param hoscode
     * @param depcode
     */
    @Override
    public void remove(String hoscode, String depcode) {
        //1、查询到要删除的科室信息
        Department department = departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode, depcode);
        //2、如果存在、获取主键id进行删除
        if(null != department) {
            //departmentRepository.delete(department);
            departmentRepository.deleteById(department.getId());
        }
    }

    /**
     * 根据医院编号，查询医院所有科室列表
     * @param hoscode
     * @return
     */
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
        Map<String, List<Department>> deparmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 deparmentMap
        for(Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> deparment1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(deparment1List.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for(Department department: deparment1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        //返回
        return result;
    }

    //根据科室编号，和医院编号，查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null) {
            return department.getDepname();
        }
        return null;
    }


    //---------------------------------------------前台用户--------------------------------------------

    /**
     *获取部门数据
     * @param hoscode
     * @param depcode
     * @return
     */
    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.findDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }

}
