package io.caoyu.emos.wx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.caoyu.emos.wx.config.SystemConstants;
import io.caoyu.emos.wx.db.dao.TbCheckinDao;
import io.caoyu.emos.wx.db.dao.TbHolidaysDao;
import io.caoyu.emos.wx.db.dao.TbWorkdayDao;
import io.caoyu.emos.wx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
}
