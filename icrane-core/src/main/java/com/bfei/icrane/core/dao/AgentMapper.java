package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.Agent;

import java.util.List;

public interface AgentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Agent record);

    int insertSelective(Agent record);

    Agent selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Agent record);

    int updateByPrimaryKey(Agent record);

    int updateAgentBalance(Agent record);

    Agent selectByUserName(String username);

    Agent selectByPhone(String phone);

    List<Agent> selectByPhoneLists(String phone);

}