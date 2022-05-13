package com.yama.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yama.yygh.common.result.Result;
import com.yama.yygh.common.util.MD5;
import com.yama.yygh.hosp.service.HospitalSetService;
import com.yama.yygh.model.hosp.HospitalSet;
import com.yama.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "医院设置管理模块")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;


    @ApiOperation("获取所有医院设置")
    @GetMapping("findAll")
    public Result<List<?>> findAllHospitalSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation("删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospitalSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if (flag){
           return Result.ok();
        }else {
           return Result.fail();
        }
    }

    /**
     * 分页查询显示医院设置
     * @param current   当前页
     * @param size     每页记录数
     * @param hospitalQueryVo   查询条件
     * @return
     */
    @ApiOperation("分页显示医院设置")
    @PostMapping("findPageHospitalSet/{current}/{size}")
    public Result findPageHospSet(
            @PathVariable Long current,
            @PathVariable Long size,
            @RequestBody (required = false) HospitalQueryVo hospitalQueryVo){
        //创建页面对象
        Page<HospitalSet> hospitalSetPage = new Page<>(current, size);
        //设置查询条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        //获取查询条件
        String hosname = hospitalQueryVo.getHosname();
        String hoscode = hospitalQueryVo.getHoscode();
        //封装查询条件
        if(!StringUtils.isEmpty(hosname)) queryWrapper.like("hosname",hosname);
        if (!StringUtils.isEmpty(hoscode)) queryWrapper.eq("hoscode",hoscode);
        //使用mybatis-plus分页插件执行分页操作
        Page<HospitalSet> page = hospitalSetService.page(hospitalSetPage, queryWrapper);
        return Result.ok(page);
    }

    @ApiOperation("添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //保存前，设置医院设置的状态，1表示可用，0表示不可用
        hospitalSet.setStatus(1);
        //生成签名密钥，用来医院接口对接检验
        int randomInt = (int)(Math.random()*1000);//生成3位整数
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+randomInt));
        //将生成的密钥保存到数据库中
        boolean saveFlag = hospitalSetService.save(hospitalSet);
        if (saveFlag) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    /**
     * 根据id获取医院设置
     * @param id
     * @return
     */
    @ApiOperation("根据id获取医院设置")
    @GetMapping("getHospitalSet/{id}")
    public Result<HospitalSet> getHospitalSet(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    @ApiOperation("更改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody(required = true) HospitalSet hospitalSet){
        //忽略空值
        //saveorUpdate方法会将空指更新替换原来的值
        boolean updateById= hospitalSetService.updateById(hospitalSet);
        if (updateById){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("批量删除医院设置")
    @DeleteMapping("batchRemoveHospitalSet")
    public Result batchRemoveHospHospitalSet(@RequestBody List<Long> idList){
        boolean ids = hospitalSetService.removeByIds(idList);
        if (ids){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(
            @PathVariable Long id,
            @PathVariable Integer status
    ){
        //根据id查找到对应的hospital设置
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //将获取到的医院设置对象进行更改状态
        hospitalSet.setStatus(status);
        //将更改后的医院设置对象重新存放
        boolean lock = hospitalSetService.updateById(hospitalSet);
        if (lock){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    /**
     * 将添加医院设置的签名密钥和医院编号进行获取，
     * 用短信进行发送，用于医院接口进行上传信息
     * @param id
     * @return
     */
    @ApiOperation("发送签名秘钥和医院编号")
    @PutMapping("sendKey/{id}")
    public Result sendKeyHospitalSet(@PathVariable Long id){
        //获取当前医院设置对象
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //获取签名密钥
        String signKey = hospitalSet.getSignKey();
        //获取医院编号
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信---等待实现的功能
        //FIXME 等待修改的功能区域
        return Result.ok();
    }

}
