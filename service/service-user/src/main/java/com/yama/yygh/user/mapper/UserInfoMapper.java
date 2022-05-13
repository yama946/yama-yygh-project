package com.yama.yygh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yama.yygh.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper//作用将mapper生成代理对象，并注入到容器中，可能引入报错，但是实际已经注入
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
