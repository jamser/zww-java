package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.SysNotify;

import java.util.List;

public interface SysNotifyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysNotify record);

    int insertSelective(SysNotify record);

    SysNotify selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysNotify record);

    int updateByPrimaryKey(SysNotify record);

    List<SysNotify> selectBySysNotifyLists();
}