package io.caoyu.emos.wx.controller;

import io.caoyu.emos.wx.common.util.R;
import io.caoyu.emos.wx.config.shiro.JwtUtil;
import io.caoyu.emos.wx.controller.form.DeleteMessageRefByIdForm;
import io.caoyu.emos.wx.controller.form.SearchMessageByIdForm;
import io.caoyu.emos.wx.controller.form.SearchMessageByPageForm;
import io.caoyu.emos.wx.controller.form.UpdateUnreadMessageForm;
import io.caoyu.emos.wx.service.MessageService;
import io.caoyu.emos.wx.task.MessageTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/message")
@Api("消息模块网络接口")
public class MessageController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTask messageTask;

    @PostMapping("/searchMessageByPage")
    @ApiOperation("获取分页消息列表")
    public R searchMessageByPage(@Valid @RequestBody SearchMessageByPageForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        int page = form.getPage();
        int length = form.getLength();
        long start = (page - 1) * length;
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return R.ok().put("result", list);
    }

    @PostMapping("/searchMessageById")
    @ApiOperation("根据ID查询消息")
    public R searchMessageById(@Valid @RequestBody SearchMessageByIdForm form) {
        HashMap map = messageService.searchMessageById(form.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/updateUnreadMessage")
    @ApiOperation("未读消息更新成已读消息")
    public R updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageForm form) {
        long rows = messageService.updateUnreadMessage(form.getId());
        return R.ok().put("result", rows == 1 ? true : false);
    }


    @PostMapping("/deleteMessageRefById")
    @ApiOperation("删除消息")
    public R deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdForm form) {
        long rows = messageService.deleteMessageRefById(form.getId());
        return R.ok().put("result", rows == 1 ? true : false);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("刷新用户的消息")
    public R refreshMessage(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        //异步接收消息
        messageTask.receiveAysnc(userId + "");
        //查询接收了多少条消息
        long lastRows = messageService.searchLastCount(userId);
        //查询未读数据
        long unreadRows = messageService.searchUnreadCount(userId);
        return R.ok().put("lastRows", lastRows).put("unreadRows", unreadRows);
    }
}

