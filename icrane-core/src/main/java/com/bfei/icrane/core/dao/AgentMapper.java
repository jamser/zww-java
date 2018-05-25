package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.Agent;

public interface AgentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Agent record);

    int insertSelective(Agent record);

    Agent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Agent record);

    int updateByPrimaryKey(Agent record);
}