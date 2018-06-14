package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.BankInfo;

import java.util.List;

public interface BankInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankInfo record);

    int insertSelective(BankInfo record);

    BankInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankInfo record);

    int updateByPrimaryKey(BankInfo record);

    BankInfo selectByCardNo(String cardNo);

    List<BankInfo> selectByAgentId(Integer agentId);

}