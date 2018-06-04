package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.AdvertisementInfoMapper;
import com.bfei.icrane.core.models.AdvertisementInfo;
import com.bfei.icrane.core.service.AdvertisementInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by moying on 2018/6/4.
 */
@Service
public class AdvertisementInfoServiceImpl implements AdvertisementInfoService {

    @Autowired
    private AdvertisementInfoMapper infoMapper;


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return infoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(AdvertisementInfo record) {
        return infoMapper.insert(record);
    }

    @Override
    public int insertSelective(AdvertisementInfo record) {
        return infoMapper.insertSelective(record);
    }

    @Override
    public AdvertisementInfo selectByPrimaryKey(Integer id) {
        return infoMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(AdvertisementInfo record) {
        return infoMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(AdvertisementInfo record) {
        return infoMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<AdvertisementInfo> selectAdInfoLists() {
        return infoMapper.selectAdInfoLists();
    }

    @Override
    public int updateByDownCount(Integer id) {
        return infoMapper.updateByDownCount(id);
    }
}
