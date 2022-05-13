package com.yama.yygh.user.api;

import com.yama.yygh.common.result.Result;
import com.yama.yygh.model.user.UserInfo;
import com.yama.yygh.service.helper.AuthContentsHelper;
import com.yama.yygh.user.service.UserInfoService;
import com.yama.yygh.vo.user.LoginVo;
import com.yama.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "用户信息管理接口")
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 会员登陆接口
     * @param loginVo
     * @param request
     * @return
     */
    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        log.info("loginVo对象："+loginVo);
//        loginVo.setIp(IpUtil.getIpAddr(request));
        Map<String, Object> info = userInfoService.login(loginVo);
        return Result.ok(info);
    }
    //用户认证接口
    @ApiOperation("用户认证操作")
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        //传递两个参数，第一个参数用户id，第二个参数认证数据vo对象
        userInfoService.userAuth(AuthContentsHelper.getUserId(request),userAuthVo);
        return Result.ok();
    }

    //获取用户id信息接口
    @ApiOperation("获取用户信息")
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = AuthContentsHelper.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }


}
