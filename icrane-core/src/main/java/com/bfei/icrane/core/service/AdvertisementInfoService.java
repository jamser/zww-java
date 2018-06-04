package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.AdvertisementInfo;

import java.util.List;

/**
 * Created by moying on 2018/6/4.
 */
public interface AdvertisementInfoService {
    int deleteByPrimaryKey(Integer id);

    int insert(AdvertisementInfo record);

    int insertSelective(AdvertisementInfo record);

    AdvertisementInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdvertisementInfo record);

    int updateByPrimaryKey(AdvertisementInfo record);

    List<AdvertisementInfo> selectAdInfoLists();

    int updateByDownCount(Integer id);
}
