package com.yama.yygh.cmn.controller;

import com.yama.yygh.cmn.Service.DictService;
import com.yama.yygh.common.result.Result;
import com.yama.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字段接口")
@RestController
@RequestMapping("admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result<List<?>> findChildData(@PathVariable Long id){
        //根据id判断是否存在子节点
        List<Dict> chlidData = dictService.findChlidData(id);
        return Result.ok(chlidData);
    }

    @ApiOperation(value="数据字典导出")
    @GetMapping(value = "/exportData")
    public Result exportData(HttpServletResponse response) {
        dictService.exportData(response);
        return Result.ok();
    }

    @ApiOperation(value = "数据字典导入")
    @PostMapping("importData")
    public Result importData(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();
    }

    /**
     * 远程调用接口1
     * @param parentDictCode
     * @param value
     * @return
     */
    @ApiOperation(value = "获取数据字典名称")
    @GetMapping(value = "/getName/{parentDictCode}/{value}")
    public String getName(
            @ApiParam(name = "parentDictCode", value = "上级编码", required = true)
            @PathVariable("parentDictCode") String parentDictCode,

            @ApiParam(name = "value", value = "值", required = true)
            @PathVariable("value") String value) {
        return dictService.getNameByParentDictCodeAndValue(parentDictCode, value);
    }

    /**
     * 远程调用接口2
     * @param value
     * @return
     */
    @ApiOperation(value = "获取数据字典名称")
    @ApiImplicitParam(name = "value", value = "值", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/getName/{value}")
    public String getName(@ApiParam(name = "value", value = "值", required = true)
            @PathVariable("value") String value) {
        return dictService.getNameByParentDictCodeAndValue("", value);
    }

    /**
     * 比如：省的dictcode是Province，可以查询子节点下所有的省份
     * @param dictCode
     * @return
     */
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping(value = "/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(
            @ApiParam(name = "dictCode", value = "节点编码", required = true)
            @PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }


}
