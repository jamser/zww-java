package com.bfei.icrane.api.service;

import com.bfei.icrane.core.models.agentWithdraw;

/**
 * Created by moying on 2018/6/6.
 */
public interface AgentWithdrawService {
    int deleteByPrimaryKey(Integer id);

    int insert(agentWithdraw record);

    int insertSelective(agentWithdraw record);

    agentWithdraw selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(agentWithdraw record);

    int updateByPrimaryKey(agentWithdraw record);

    Long selectByWithdraw(Integer id);
}
