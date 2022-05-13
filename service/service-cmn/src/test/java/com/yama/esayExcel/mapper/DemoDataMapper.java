package com.yama.esayExcel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yama.esayExcel.entity.DemoData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DemoDataMapper extends BaseMapper<DemoData> {
}
