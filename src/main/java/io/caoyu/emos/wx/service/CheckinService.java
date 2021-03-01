package io.caoyu.emos.wx.service;

import java.util.HashMap;

public interface CheckinService {
    public String validCanCheckinIn(int userId,String date);
    public void checkin(HashMap param);
}
