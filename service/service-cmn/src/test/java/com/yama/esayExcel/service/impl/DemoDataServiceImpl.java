package com.yama.esayExcel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yama.esayExcel.entity.DemoData;
import com.yama.esayExcel.mapper.DemoDataMapper;
import com.yama.esayExcel.service.DemoDataService;
import org.springframework.stereotype.Service;

@Service
public class DemoDataServiceImpl extends ServiceImpl<DemoDataMapper, DemoData> implements DemoDataService {
}
