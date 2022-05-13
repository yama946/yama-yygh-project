package com.yama.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.yama.yygh.common.exception.YyghException;
import com.yama.yygh.common.helper.JwtHelper;
import com.yama.yygh.common.result.Result;
import com.yama.yygh.common.result.ResultCodeEnum;
import com.yama.yygh.model.user.UserInfo;
import com.yama.yygh.user.service.UserInfoService;
import com.yama.yygh.user.util.ConstantUserPropertiesUtil;
import com.yama.yygh.user.util.HttpClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口1:返回数据用来显示二维码
 * 前端要显示二维码，微信提供了其所需的js文件，而要显示还需要请求相关参数，也就是申请凭证所需要的信息
 * 此接口就用来返回这些参数信息。
 * appid	是	应用唯一标识，在微信开放平台提交应用审核通过后获得
 * redirect_uri	是	重定向地址，需要进行UrlEncode
 * state	否	用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），
 * 建议第三方带上该参数，可设置为简单的随机数加session进行校验
 */

@Api(tags = "微信登陆接口")
@Controller
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class WeChatApiController {
    @Autowired
    private UserInfoService userInfoService;


    /**
     * 获取微信二维码产生参数
     * @param session
     * @return
     */
    @ApiOperation("返回参数，生成二维码")
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantUserPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid", ConstantUserPropertiesUtil.WX_OPEN_APP_ID);
        paramMap.put("redirect_uri",redirectUri);
        paramMap.put("scope", "snsapi_login");
        paramMap.put("stat",System.currentTimeMillis()+"");
        return Result.ok(paramMap);
    }

    /**
     * 微信登录回调
     *
     * @param code
     * @param state
     * @return
     */
    @ApiOperation("微信回调接口，获取扫描人信息 ")
    @RequestMapping("callback")
    public String callback(String code, String state) {
        //获取授权临时票据
        System.out.println("微信授权服务器回调。。。。。。");
        System.out.println("state = " + state);
        System.out.println("code = " + code);

        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)) {
            log.error("非法回调请求");
            throw new YyghException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        //通过String工具类拼接字符串上的占位符
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantUserPropertiesUtil.WX_OPEN_APP_ID,
                ConstantUserPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        String result = null;
        try {
            result = HttpClientUtil.get(accessTokenUrl);
        } catch (Exception e) {
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        System.out.println("使用code换取的access_token结果 = " + result);
        //获取到json返回值数据，进行处理转换成对象
        JSONObject resultJson = JSONObject.parseObject(result);
        if(resultJson.getString("errcode") != null){
            log.error("获取access_token失败：" + resultJson.getString("errcode") + resultJson.getString("errmsg"));
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        String accessToken = resultJson.getString("access_token");
        String openId = resultJson.getString("openid");
        log.info(accessToken);
        log.info(openId);

        //根据access_token获取微信用户的基本信息
        //先根据openid进行数据库查询
         UserInfo userInfo = userInfoService.getByOpenid(openId);
        // 如果没有查到用户信息,那么调用微信个人信息获取的接口
         if(null == userInfo) {
             //如果查询到个人信息，那么直接进行登录
             //使用access_token换取受保护的资源：微信的个人信息
             //再次发送请求
             String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                     "?access_token=%s" +
                     "&openid=%s";
             String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);
             String resultUserInfo = null;
             try {
                 resultUserInfo = HttpClientUtil.get(userInfoUrl);
             } catch (Exception e) {
                 throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
             }
             System.out.println("使用access_token获取用户信息的结果 = " + resultUserInfo);
             //获取到扫描人信息json进行处理
             JSONObject resultUserInfoJson = JSONObject.parseObject(resultUserInfo);
             if (resultUserInfoJson.getString("errcode") != null) {
                 log.error("获取用户信息失败：" + resultUserInfoJson.getString("errcode") + resultUserInfoJson.getString("errmsg"));
                 throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
             }
             //解析用户信息
             String nickname = resultUserInfoJson.getString("nickname");
             String headimgurl = resultUserInfoJson.getString("headimgurl");

             userInfo = new UserInfo();
             userInfo.setOpenid(openId);
             userInfo.setNickName(nickname);
             userInfo.setStatus(1);
             //保存用户数据
             userInfoService.save(userInfo);
              }
             //创建集合保存用户信息，交给前端进行保存到cookie
             Map<String, Object> map = new HashMap<>();
             String name = userInfo.getName();
             if (StringUtils.isEmpty(name)) {
                 name = userInfo.getNickName();
             }
             if (StringUtils.isEmpty(name)) {
                 name = userInfo.getPhone();
             }
             map.put("name", name);
             if (StringUtils.isEmpty(userInfo.getPhone())) {
                 map.put("openid", userInfo.getOpenid());
             } else {
                 map.put("openid", "");
             }
             String token = JwtHelper.createToken(userInfo.getId(), name);
             map.put("token", token);
        //将前端页面重定向
        return "redirect:" + ConstantUserPropertiesUtil.YYGH_BASE_URL + "/weixin/callback?token="+map.get("token")+"&openid="+map.get("openid")+"&name="+URLEncoder.encode((String)map.get("name"));
    }


}
