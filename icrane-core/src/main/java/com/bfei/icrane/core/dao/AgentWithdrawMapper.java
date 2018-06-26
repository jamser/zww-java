package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.AgentWithdraw;

import java.util.List;

public interface AgentWithdrawMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AgentWithdraw record);

    int insertSelective(AgentWithdraw record);

    AgentWithdraw selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AgentWithdraw record);

    int updateByPrimaryKey(AgentWithdraw record);

    Long selectByWithdraw(Integer id);

    List<AgentWithdraw>selectByWithdrawLists(Integer id);
}