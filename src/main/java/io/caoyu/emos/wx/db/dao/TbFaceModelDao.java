package io.caoyu.emos.wx.db.dao;

import io.caoyu.emos.wx.db.pojo.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbFaceModelDao {
    public String searchFaceModel(int userId);

    public void insertFaceModel(TbFaceModel tbFaceModel);

    public void deleteFaceModel(int userId);
}