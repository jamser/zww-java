package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.AgentCharge;

import java.util.List;

public interface AgentChargeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AgentCharge record);

    int insertSelective(AgentCharge record);

    AgentCharge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentCharge record);

    int updateByPrimaryKey(AgentCharge record);

    long selectByAgentSuperId(Integer agentId);

    long selectByAgentOneId(Integer agentId);

    long selectByAgentTwoId(Integer agentId);

    long selectByAgentThreeId(Integer agentId);

    List<AgentCharge> selectIncomeByAgentId(Integer agentId);
}