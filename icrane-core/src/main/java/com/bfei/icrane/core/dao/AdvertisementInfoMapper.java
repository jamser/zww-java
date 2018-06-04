package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.AdvertisementInfo;

import java.util.List;

public interface AdvertisementInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdvertisementInfo record);

    int insertSelective(AdvertisementInfo record);

    AdvertisementInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdvertisementInfo record);

    int updateByPrimaryKey(AdvertisementInfo record);

    List<AdvertisementInfo> selectAdInfoLists();

    int updateByDownCount(Integer id);
}