package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.Agent;

/**
 * Created by moying on 2018/5/25.
 */
public interface AgentService {
    int deleteByPrimaryKey(Long id);

    int insert(Agent record);

    int insertSelective(Agent record);

    Agent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Agent record);

    int updateByPrimaryKey(Agent record);
}
