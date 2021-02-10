package io.caoyu.emos.wx.controller;

import cn.hutool.core.date.DateUtil;
import io.caoyu.emos.wx.common.util.R;
import io.caoyu.emos.wx.config.shiro.JwtUtil;
import io.caoyu.emos.wx.service.CheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/checkin")
@RestController
@Slf4j
@Api("签到模块接口")
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户今天是否可以签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckinIn(userId, DateUtil.today());
        return R.ok(result);
    }
}
