package com.yama.yygh.user.service.iml;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yama.yygh.cmn.client.DictFeignClient;
import com.yama.yygh.enums.DictEnum;
import com.yama.yygh.model.user.Patient;
import com.yama.yygh.user.mapper.PatientMapper;
import com.yama.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Autowired
    private DictFeignClient dictFeignClient;

    /**
     * 根据用户列表插叙所有的就诊人
     * @param userId
     * @return
     */
    @Override
    public List<Patient> findAllUserId(Long userId) {
        //根据uid进行查询就诊人信息，封装成集合
        //mp中对于非主键查询，需要查询构造器
        QueryWrapper<Patient> patientWrapper = new QueryWrapper<>();
        patientWrapper.eq("user_id",userId);
        List<Patient> patients = baseMapper.selectList(patientWrapper);
        //将结合转换成流的形式，对其中元素进行遍历
        patients.stream().forEach((item)->this.packPatient(item));
        return patients;
    }

    /**
     * 获取单个就诊人信息根据id
     * @param id
     * @return
     */
    @Override
    public Patient getPatientId(Long id) {
        return this.packPatient(baseMapper.selectById(id));
    }

    /**
     * 通过远程方法调用将patient中，参数进行补全
     * @param patient
     * @return
     */
    private Patient packPatient(Patient patient){
        //根据证件类型编码，获取证件类型
        String certificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),
                        patient.getCertificatesType());
        //联系人证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //联系人所在省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //联系人所在市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //联系人区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }

}
