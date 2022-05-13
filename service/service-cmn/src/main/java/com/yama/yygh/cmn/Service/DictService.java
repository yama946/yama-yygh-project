package com.yama.yygh.cmn.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yama.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 用来查询所有的数据字段数据，树形显示
 */
public interface DictService extends IService<Dict> {
    //根据数据id查询子数据列表
    List<Dict> findChlidData(Long id);

    /**
     * 数据字典导出
     * @param
     */
    void exportData(HttpServletResponse response);

    /**
     * 数据字典导入
     * @param file
     */
    void importDictData(MultipartFile file);

    /**
     * 根据上级编码与值获取数据字典名称，用于远程方法调用
     * @param parentDictCode
     * @param value
     * @return
     */
    String getNameByParentDictCodeAndValue(String parentDictCode, String value);

    /**
     * 根据dictcode查询字典对象
     * @param dictCode
     * @return
     */
    List<Dict> findByDictCode(String dictCode);
}
