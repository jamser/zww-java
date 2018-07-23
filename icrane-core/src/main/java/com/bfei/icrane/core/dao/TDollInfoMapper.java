package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.TDollInfo;

public interface TDollInfoMapper {
    int deleteByPrimaryKey(Integer id);

    TDollInfo selectByollCode(String dollCode);

    void updateByDollCode(TDollInfo record);

    int insert(TDollInfo record);

    int insertSelective(TDollInfo record);

    TDollInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TDollInfo record);

    int updateByPrimaryKeyWithBLOBs(TDollInfo record);

    int updateByPrimaryKey(TDollInfo record);
}