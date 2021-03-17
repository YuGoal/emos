package io.caoyu.emos.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbWorkdayDao {
    public Integer searchTodayIsWorkday();

    public ArrayList<String> searchWorkdayInRange(HashMap param);
}