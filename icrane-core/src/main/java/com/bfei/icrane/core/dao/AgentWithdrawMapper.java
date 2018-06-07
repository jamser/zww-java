package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.agentWithdraw;

public interface AgentWithdrawMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(agentWithdraw record);

    int insertSelective(agentWithdraw record);

    agentWithdraw selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(agentWithdraw record);

    int updateByPrimaryKey(agentWithdraw record);

    Long selectByWithdraw(Integer id);
}