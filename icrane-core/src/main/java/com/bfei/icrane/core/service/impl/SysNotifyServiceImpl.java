package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.SysNotifyMapper;
import com.bfei.icrane.core.models.SysNotify;
import com.bfei.icrane.core.service.SysNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by moying on 2018/6/4.
 */
@Service
public class SysNotifyServiceImpl implements SysNotifyService {


    @Autowired
    private SysNotifyMapper sysNotifyMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return sysNotifyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(SysNotify record) {
        return sysNotifyMapper.insert(record);
    }

    @Override
    public int insertSelective(SysNotify record) {
        return sysNotifyMapper.insertSelective(record);
    }

    @Override
    public SysNotify selectByPrimaryKey(Integer id) {
        return sysNotifyMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SysNotify record) {
        return sysNotifyMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(SysNotify record) {
        return sysNotifyMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<SysNotify> selectBySysNotifyLists() {
        return sysNotifyMapper.selectBySysNotifyLists();
    }
}
