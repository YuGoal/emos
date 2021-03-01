package io.caoyu.emos.wx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.caoyu.emos.wx.config.SystemConstants;
import io.caoyu.emos.wx.db.dao.TbCheckinDao;
import io.caoyu.emos.wx.db.dao.TbFaceModelDao;
import io.caoyu.emos.wx.db.dao.TbHolidaysDao;
import io.caoyu.emos.wx.db.dao.TbWorkdayDao;
import io.caoyu.emos.wx.db.exception.EmosException;
import io.caoyu.emos.wx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private SystemConstants systemConstants;
    @Autowired
    private TbCheckinDao tbCheckinDao;
    @Autowired
    private TbHolidaysDao tbHolidaysDao;
    @Autowired
    private TbWorkdayDao tbWorkdayDao;

    @Autowired
    private TbFaceModelDao faceModelDao;
    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Override
    public String validCanCheckinIn(int userId, String date) {
        boolean bool_1 = tbHolidaysDao.searchTodayIsHolidays() != null ? true : false;//是否是节假日
        boolean bool_2 = tbWorkdayDao.searchTodayIsWorkday() != null ? true : false;//是否是工作日

        String type = "节假日";
        if (DateUtil.date().isWeekend()) {
            type = "节假日";
        }
        if (bool_1) {
            type = "节假日";
        } else if (bool_2) {
            type = "工作日";
        }
        if (type.equals("节假日")) {
            return "节假日不需要考勤";
        } else {
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String end = DateUtil.today() + " " + systemConstants.attendanceEndTime;
            DateTime attendanceStartTime = DateUtil.parseTime(start);
            DateTime attendanceEndTime = DateUtil.parseTime(end);
            if (now.isBefore(attendanceStartTime)) {
                return "没到上班时间";
            } else if (now.isAfter(attendanceEndTime)) {
                return "上班时间考勤已经结束";
            } else {
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("date", date);
                map.put("start", start);
                map.put("end", end);
                boolean bool = tbCheckinDao.haveCheckin(map) != null ? true : false;
                return bool ? "今天已经打卡，不用重复打卡" : "可以打卡";
            }
        }
    }

    @Override
    public void checkin(HashMap param) {
        //判断签到
        Date d1 = DateUtil.date();  //当前时间
        Date d2 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime);  //上班时间
        Date d3 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime); //签到结束时间
        int status = 1;
        if (d1.compareTo(d2) <= 0) {
            status = 1; // 正常签到
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2; //迟到
        }
        //查询签到人的人脸模型数据
        int userId= (Integer) param.get("userId");
        String faceModel=faceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("不存在人脸模型");
        } else {
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                log.error("人脸识别服务异常");
                throw new EmosException("人脸识别服务异常");
            }
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("签到无效，非本人签到");
            } else if ("True".equals(body)) {
                //TODO 这里要获取签到地区新冠疫情风险等级
                //TODO 保存签到记录
            }
        }
    }
}
