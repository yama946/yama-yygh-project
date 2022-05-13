package com.yama.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yama.yygh.common.result.Result;
import com.yama.yygh.hosp.service.HospitalService;
import com.yama.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预约平台后台管理系统调用接口
 */
@Api(tags = "医院信息管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 查询所有注册的医院信息，在后台管理系统中进行显示
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("list/{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,

            @ApiParam(name = "hospitalQueryVo", value = "查询对象", required = false)
             @RequestBody(required = false) HospitalQueryVo hospitalQueryVo) {
        return Result.ok(hospitalService.selectPage(page, limit, hospitalQueryVo));
    }

    /**
     * 更新mongodb中医院的线上状态
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "更新上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result lock(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable("id") String id,
            @ApiParam(name = "status", value = "状态（0：未上线 1：已上线）", required = true)
            @PathVariable("status") Integer status){
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 获取医院详情信息
     * @param id
     * @return
     */
    @ApiOperation(value = "获取医院详情")
    @GetMapping("show/{id}")
    public Result show(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable String id) {
        return Result.ok(hospitalService.show(id));
    }



}

