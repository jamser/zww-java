package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.AgentCharge;

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
}
