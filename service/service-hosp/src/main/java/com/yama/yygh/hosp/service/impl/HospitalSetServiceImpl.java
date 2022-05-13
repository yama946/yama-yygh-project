package com.yama.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yama.yygh.common.exception.YyghException;
import com.yama.yygh.common.result.ResultCodeEnum;
import com.yama.yygh.hosp.mapper.HospitalSetMapper;
import com.yama.yygh.hosp.service.HospitalSetService;
import com.yama.yygh.model.hosp.HospitalSet;
import com.yama.yygh.vo.order.SignInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hospitalSetService")
@Slf4j
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    /**
     * 获取当前医院的签名
     * @param hoscode
     * @return
     */
    @Override
    public String getSignKey(String hoscode) {
        HospitalSet hospitalSet = this.getByHoscode(hoscode);
        if (hospitalSet==null){
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        if (hospitalSet.getStatus().intValue()==0){
            throw new YyghException(ResultCodeEnum.HOSPITAL_LOCK);
        }
        return hospitalSet.getSignKey();
    }

    /**
     * 上传医院中获取hoscode医院编号，获取医院设置信息
     * 从mysql中获取医院设置信息
     * @param hoscode
     * @return
     */
    public HospitalSet getByHoscode(String hoscode){
        return hospitalSetMapper.selectOne(new QueryWrapper<HospitalSet>().eq("hoscode",hoscode));
    }

    //--------------------------------远程调用依赖方法-------------------------------------
    //获取医院签名信息
    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if(null == hospitalSet) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }

}
