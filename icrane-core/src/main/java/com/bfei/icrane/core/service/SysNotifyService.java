package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.SysNotify;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by moying on 2018/6/4.
 */

public interface SysNotifyService {
    int deleteByPrimaryKey(Integer id);

    int insert(SysNotify record);

    int insertSelective(SysNotify record);

    SysNotify selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysNotify record);

    int updateByPrimaryKey(SysNotify record);

    List<SysNotify> selectBySysNotifyLists();
}
