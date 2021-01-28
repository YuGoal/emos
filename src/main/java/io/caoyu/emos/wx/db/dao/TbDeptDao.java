package io.caoyu.emos.wx.db.dao;

import io.caoyu.emos.wx.db.pojo.TbDept;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbDeptDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbDept record);

    int insertSelective(TbDept record);

    TbDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbDept record);

    int updateByPrimaryKey(TbDept record);
}