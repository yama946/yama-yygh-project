package com.yama.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yama.yygh.cmn.client.DictFeignClient;
import com.yama.yygh.common.exception.YyghException;
import com.yama.yygh.common.result.ResultCodeEnum;
import com.yama.yygh.enums.DictEnum;
import com.yama.yygh.hosp.repository.HospitalRepository;
import com.yama.yygh.hosp.service.DepartmentService;
import com.yama.yygh.hosp.service.HospitalService;
import com.yama.yygh.model.hosp.BookingRule;
import com.yama.yygh.model.hosp.Department;
import com.yama.yygh.model.hosp.Hospital;
import com.yama.yygh.model.hosp.Schedule;
import com.yama.yygh.vo.hosp.BookingScheduleRuleVo;
import com.yama.yygh.vo.hosp.HospitalQueryVo;
import lombok.extern.slf4j.Slf4j;
import com.yama.yygh.common.util.BeanUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.*;
import java.util.stream.Collectors;

@Service("hospitalService")
@Slf4j
public class HospitalServiceImpl implements HospitalService {
    /**
     * 注入mongodb的连接操作工具，也就是dao层接口
     */
    @Autowired
    private HospitalRepository hospitalRepository;
    /**
     * 远程方法调用接口
     */
    @Autowired
    private DictFeignClient dictFeignClient;


    /**
     * 保存上传的医院信息
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        //打印日志：请求上传的医院信息打印
        log.info(JSONObject.toJSONString(paramMap));
        //将上传的医院信息，转型成hospital对象
        Hospital hospital = JSONObject.parseObject((JSON.toJSONString(paramMap)), Hospital.class);
        //通过hoscode判断判断是否存在，存在进行更新，不存在进行插入保存
        Hospital targetHospital = hospitalRepository.findHospitalByHoscode(hospital.getHoscode());
        if (null != targetHospital){
//            hospital.setId(targetHospital.getId());
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(targetHospital.getIsDeleted());
            hospitalRepository.save(hospital);
            return;
        }else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
            return;
        }
    }

    /**
     * 查询医院信息用来显示
     * @param hoscode
     * @return
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.findHospitalByHoscode(hoscode);
    }

    /**
     * 查询医院信息进行分页显示
     * @param page 当前页码
     * @param limit 每页记录数
     * @param hospitalQueryVo 查询条件
     * @return
     */
    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        Hospital hospital = new Hospital();
        //如果使用spring的copy属性，值不能为空，改造后可以为空
        if (hospitalQueryVo!=null){
            BeanUtils.copyProperties(hospitalQueryVo, hospital);
        }

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //创建查询条件
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        //远程方法调用，获取省市，医院等信息
        pages.getContent().stream().forEach(item -> {
            this.packHospital(item);
        });


        return pages;
    }

    /**
     * 封装数据
     * @param hospital
     * @return
     */
    private Hospital packHospital(Hospital hospital) {
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(),hospital.getHostype());
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }

    /**
     * 更新医院线上状态
     * bug：当前线上状态和mysql数据库中的状态不一致，未同步更改
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(String id, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 查询医院的详情
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> show(String id) {
        Map<String, Object> result = new HashMap<>();

        Hospital hospital = this.packHospital(this.getById(id));
        result.put("hospital", hospital);

        //单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    /**
     * 根据id查询要显示的医院对象
     * @param id
     * @return
     */
    private Hospital getById(String id) {
        //查询医院
        Optional<Hospital> optHospital = hospitalRepository.findById(id);
        if (!optHospital.isPresent()){
            throw new YyghException("mongodb数据库中没有此医院的信息,请检查后重新输入");
        }
        return optHospital.get();
    }

    /**
     * 根据医院编号获取医院名称
     * @param hoscode
     * @return
     */
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.findHospitalByHoscode(hoscode);
        if(null != hospital) {
            return hospital.getHosname();
        }
        return "";
    }

    /**
     * 根据医院名获取医院列表
     * @param hosname
     * @return
     */
    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    /**
     * 医院预约挂号详情
     * @param hoscode
     * @return
     */
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.packHospital(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }





}
