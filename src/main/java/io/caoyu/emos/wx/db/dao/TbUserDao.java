package io.caoyu.emos.wx.db.dao;

import io.caoyu.emos.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Set;

@Mapper
public interface TbUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbUser record);

    int insertSelective(TbUser record);

    TbUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbUser record);

    int updateByPrimaryKey(TbUser record);

    //判断超级管理员是否已经绑定
    boolean haveRootUser();
    //插入用户数据
    int insertUser(HashMap map);
    //查询用户id
    int searchIdByOpenId(String openId);

    public Set<String> searchUserPermissions(int userId);
}