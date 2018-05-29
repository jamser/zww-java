package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.OemBanner;

import java.util.List;

public interface OemBannerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OemBanner record);

    int insertSelective(OemBanner record);

    OemBanner selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OemBanner record);

    int updateByPrimaryKey(OemBanner record);

    List<OemBanner> selectByOemId(Integer oemId);

    List<OemBanner> selectAllOemBanner();
}