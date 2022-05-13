package com.yama.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yama.yygh.model.user.UserInfo;
import com.yama.yygh.vo.user.LoginVo;
import com.yama.yygh.vo.user.UserAuthVo;
import com.yama.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    /**
     * 用户登陆
     * @param loginVo
     * @return
     */
    Map<String, Object> login(LoginVo loginVo);

    /**
     * 根据微信openid获取用户信息
     * @param openid
     * @return
     */
    UserInfo getByOpenid(String openid);

    /**
     * 用户认证操作
     * @param userId
     * @param userAuthVo
     */
    void userAuth(Long userId, UserAuthVo userAuthVo);

    /**
     * 用户信息分页操作
     * @param pageParam 分页对象
     * @param userInfoQueryVo 分页条件
     * @return
     */
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    /**
     * 锁定用户
     * @param userId
     * @param status
     */
    void lock(Long userId, Integer status);

    /**
     * 获取当前用户个人信息和就诊人信息
     * @param userId
     * @return
     */
    Map<String, Object> show(Long userId);

    /**
     * 用户认证审批
     * @param userId
     * @param authStatus
     */
    void approval(Long userId, Integer authStatus);
}
