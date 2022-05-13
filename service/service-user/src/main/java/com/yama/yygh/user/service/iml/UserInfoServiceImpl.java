package com.yama.yygh.user.service.iml;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yama.yygh.cmn.client.DictFeignClient;
import com.yama.yygh.common.exception.YyghException;
import com.yama.yygh.common.helper.JwtHelper;
import com.yama.yygh.common.result.ResultCodeEnum;
import com.yama.yygh.enums.AuthStatusEnum;
import com.yama.yygh.enums.DictEnum;
import com.yama.yygh.model.user.Patient;
import com.yama.yygh.model.user.UserInfo;
import com.yama.yygh.user.mapper.UserInfoMapper;
import com.yama.yygh.user.service.PatientService;
import com.yama.yygh.user.service.UserInfoService;
import com.yama.yygh.vo.user.LoginVo;
import com.yama.yygh.vo.user.UserAuthVo;
import com.yama.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper,UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private DictFeignClient dictFeignClient;


    @Autowired
    private PatientService patientService;

    /**
     * 用户的登陆操作
     * @param loginVo
     * @return
     */
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //校验参数
        if(StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验校验验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }
        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.getByOpenid(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        //userInfo=null 说明手机直接登录
        if(null == userInfo) {

            //手机号已被使用
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            //获取会员
            userInfo = userInfoMapper.selectOne(queryWrapper);
            if (null == userInfo) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                this.save(userInfo);
            }
        }
        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //jwt生成token字符串，存放到cookie中用来进行登陆校验
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);

        return map;
    }

    /**
     * 通过openid查询是否存在用户，不存在进行手机绑定
     * @param openid
     * @return
     */
    @Override
    public UserInfo getByOpenid(String openid) {
        return userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("openid", openid));
    }

    /**
     * 用户认证
     * @param userId
     * @param userAuthVo
     */
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据id查询注册用户
        UserInfo userInfo = userInfoMapper.selectById(userId);
        //设置用户信息
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //将用户信息进行更新保存
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 后台：对用户信息进行分页
     * @param pageParam 分页对象
     * @param userInfoQueryVo 分页条件
     * @return
     */
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //创建查询构造器
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //调用mapper的方法
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;
    }

    /**
     * 将userinfo对象中的编号变成对应值
     * @param userInfo
     * @return
     */
    private UserInfo packageUserInfo(UserInfo userInfo) {
        String certificatesType = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),userInfo.getCertificatesType());
        userInfo.getParam().put("certificatesTypeString", certificatesType);
        //处理认证状态编码
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue()==0 ?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;
    }

    /**
     * 锁定用户信息
     * @param userId    用户id
     * @param status    用户达到的状态
     */
    @Override
    public void lock(Long userId, Integer status) {
        //通过id获取用户对象
        UserInfo userInfo = baseMapper.selectById(userId);
        //通过mapper操作数据库
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    /**
     * 获取当前用户个人信息和就诊人信息
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> show(Long userId) {
        HashMap<String, Object> map = new HashMap<>();
        //根据uid获取当前用户信息
        UserInfo userInfo = userInfoMapper.selectById(userId);
        map.put("userInfo",userInfo);
        //根据uid获取当前用户关联的就诊人信息
        List<Patient> patients = patientService.findAllUserId(userId);
        map.put("patientList",patients);
        return map;
    }

    /**
     * 用户认证审批
     * 认证审批  2通过  -1不通过
     * @param userId
     * @param authStatus
     */
    @Override
    public void approval(Long userId, Integer authStatus) {
        //判断当前状态是否为非法状态
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

}
