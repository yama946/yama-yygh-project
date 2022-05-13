package com.yama.yygh.cmn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yama.yygh.model.cmn.Dict;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DictMapper extends BaseMapper<Dict> {
    /**
     * 这里我们也可定义mybatis-plus中不存在的方法，
     * 在xml文件中定义响应的sql语句，或者用mybatis注解的形式
     */

}
