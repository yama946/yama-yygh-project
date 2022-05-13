package com.yama.yygh.user.api;

import com.yama.yygh.common.result.Result;
import com.yama.yygh.model.user.Patient;
import com.yama.yygh.service.helper.AuthContentsHelper;
import com.yama.yygh.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//就诊人管理接口
@Api(tags = "前台就诊人管理接口")
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {
    @Autowired
    private PatientService patientService;

    /**
     * 获取就诊人列表
     * @param request
     * @return
     */
    @ApiOperation("获取就诊人列表")
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request) {
        //根据reques获取用户id
        Long userId = AuthContentsHelper.getUserId(request);
        List<Patient> list = patientService.findAllUserId(userId);
        return Result.ok(list);
    }
    //添加就诊人
    @ApiOperation("添加就诊人")
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        //获取当前用户的id
        Long userId = AuthContentsHelper.getUserId(request);
        //设置就诊人所属的uid
        patient.setUserId(userId);
        //保存就诊信息到数据库
        patientService.save(patient);
        return Result.ok();
    }

    //根据id获取就诊人信息
    @ApiOperation("根据id获取就诊人信息")
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatientId(id);
        return Result.ok(patient);
    }
    //修改就诊人
    @ApiOperation("修改就诊人")
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }
    //删除就诊人
    @ApiOperation("删除就诊人")
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id) {
        //根据id进行删除就诊人
        patientService.removeById(id);
        return Result.ok();
    }

    //----------------------------获取订单信息远程调用---------------------------------

    /**
     * 获取当前就诊人信息
     * @param id
     * @return
     */
    @ApiOperation(value = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(
            @ApiParam(name = "id", value = "就诊人id", required = true)
            @PathVariable("id") Long id) {
        return patientService.getById(id);
    }


}
