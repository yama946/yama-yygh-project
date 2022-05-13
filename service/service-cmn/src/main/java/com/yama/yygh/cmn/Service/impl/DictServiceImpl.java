package com.yama.yygh.cmn.Service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yama.yygh.cmn.Service.DictService;
import com.yama.yygh.cmn.listener.DictListener;
import com.yama.yygh.cmn.mapper.DictMapper;
import com.yama.yygh.common.exception.YyghException;
import com.yama.yygh.model.cmn.Dict;
import com.yama.yygh.vo.cmn.DictEeVo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DictMapper dictMapper;

    /**
     * 根据编号id获取子节点，进行懒加载所有的数据字典数据
     * @param id
     * @return
     */
    @Override
    public List<Dict> findChlidData(Long id) {
        //由于mp无法使用非主键查询，我们需要自定义条件构造器
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        //通过条件构造器进行查询数据
        List<Dict> dictList = dictMapper.selectList(queryWrapper);
        ////向list集合每个dict对象中设置hasChildren，用于element-ui进行界面显示
        for (Dict dict : dictList) {
            //根据当前dict获取id判断是否存在子节点
            Long dictId = dict.getId();
            boolean hasChildren = this.isChildren(dictId);
            dict.setHasChildren(hasChildren);
        }
        return dictList;
    }

    /**
     * 判断当前节点是否存在字节点
     * @param id
     * @return
     */
    private boolean isChildren(Long id){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer count = dictMapper.selectCount(queryWrapper);
        return (count>0)?true:false;
    }


    /**
     * 数据字典导出
     * @param response
     */
    @Override
    public void exportData(HttpServletResponse response){
        try{
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            List<Dict> dictList = dictMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for(Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictVo, DictEeVo.class);
                dictVoList.add(dictVo);
            }
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("模板").doWrite(dictList);
        }catch (IOException e){
            throw new YyghException("数据字典导出失败");
        }

    }

    /**
     * 数据字典导入
     * allEntries = true: 方法调用后清空所有缓存
     * @param file
     */
    @CacheEvict(value = "dict", allEntries=true)
    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(dictMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
        //如果value能唯一定位数据字典，parentDictCode可以传空，例如：省市区的value值能够唯一确定
        if(StringUtils.isEmpty(parentDictCode)) {
            Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
            if(null != dict) {
                return dict.getName();
            }
        } else {
            //当不能通过value唯一确定，可以通过父id和value值来定位一个dict
            Dict parentDict = this.getByDictCode(parentDictCode);
            if(null == parentDict) return "";
            Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parentDict.getId()).eq("value", value));
            if(null != dict) {
                return dict.getName();
            }
        }
        return "";
    }

    /**
     *
     * 根据父编号，获取当前字典
     * @param parentDictCode
     * @return
     */
    private Dict getByDictCode(String parentDictCode) {
        Dict dict = dictMapper.selectOne(new QueryWrapper<Dict>().eq("dict_code", parentDictCode));
        return dict;
    }

    /**
     * 根据dictcode获取子数据列表
     * @param dictCode
     * @return
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict codeDict = this.getByDictCode(dictCode);
        if(null == codeDict) return null;
        return this.findChlidData(codeDict.getId());
    }


}
