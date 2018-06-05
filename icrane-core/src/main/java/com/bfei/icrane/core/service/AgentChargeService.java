package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.AgentCharge;

import java.util.List;

/**
 * Created by moying on 2018/5/25.
 */
public interface AgentChargeService {
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

    List<AgentCharge> selectByStatus(Integer status);

    int updateStatus(Long id);
}
