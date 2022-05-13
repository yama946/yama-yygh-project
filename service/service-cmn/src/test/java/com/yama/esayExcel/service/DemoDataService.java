package com.yama.esayExcel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yama.esayExcel.entity.DemoData;
import com.yama.esayExcel.mapper.DemoDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 假设这个是你的DAO存储。当然还要这个类让spring管理，当然你不用需要存储，也不需要这个类。
 **/
public interface DemoDataService extends IService<DemoData> {
}
