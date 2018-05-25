package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.AgentCharge;

public interface AgentChargeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AgentCharge record);

    int insertSelective(AgentCharge record);

    AgentCharge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentCharge record);

    int updateByPrimaryKey(AgentCharge record);
}