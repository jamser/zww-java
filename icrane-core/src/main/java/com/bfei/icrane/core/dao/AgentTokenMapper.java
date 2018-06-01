package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.AgentToken;

public interface AgentTokenMapper {
    int deleteByPrimaryKey(String token);

    int insert(AgentToken record);

    int insertSelective(AgentToken record);

    AgentToken selectByToken(String token);

    int updateByPrimaryKeySelective(AgentToken record);

    int updateByPrimaryKey(AgentToken record);

    AgentToken selectByAgentId(Integer agentId);

    int deleteByAgentId(Integer agentId);
}