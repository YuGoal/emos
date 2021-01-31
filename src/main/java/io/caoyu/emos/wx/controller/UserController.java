package io.caoyu.emos.wx.controller;

import io.caoyu.emos.wx.common.util.R;
import io.caoyu.emos.wx.config.shiro.JwtUtil;
import io.caoyu.emos.wx.controller.form.LoginForm;
import io.caoyu.emos.wx.controller.form.RegisterForm;
import io.caoyu.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterForm form) {
        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
        String token = jwtUtil.createToken(id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        saveCache(token, id);
        return R.ok().put("token", token).put("permission", permsSet);
    }

    private void saveCache(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R login(@Valid @RequestBody LoginForm form){
        int id = userService.login(form.getCode());
        String token = jwtUtil.createToken(id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        saveCache(token, id);
        return R.ok("登录成功").put("token", token).put("permission", permsSet);
    }

}
