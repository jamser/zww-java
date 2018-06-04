package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.AgentChargeMapper;
import com.bfei.icrane.core.models.AgentCharge;
import com.bfei.icrane.core.service.AgentChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by moying on 2018/5/25.
 */
@Service("AgentChargeService")
public class AgentChargeServiceImpl implements AgentChargeService {

    @Autowired
    private AgentChargeMapper agentChargeMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return agentChargeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(AgentCharge record) {
        return agentChargeMapper.insert(record);
    }

    @Override
    public int insertSelective(AgentCharge record) {
        return agentChargeMapper.insertSelective(record);
    }

    @Override
    public AgentCharge selectByPrimaryKey(Long id) {
        return agentChargeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(AgentCharge record) {
        return agentChargeMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(AgentCharge record) {
        return agentChargeMapper.updateByPrimaryKey(record);
    }

    @Override
    public long selectByAgentSuperId(Integer agentId) {
        return agentChargeMapper.selectByAgentSuperId(agentId);
    }

    @Override
    public long selectByAgentOneId(Integer agentId) {
        return agentChargeMapper.selectByAgentOneId(agentId);
    }

    @Override
    public long selectByAgentTwoId(Integer agentId) {
        return agentChargeMapper.selectByAgentTwoId(agentId);
    }

    @Override
    public long selectByAgentThreeId(Integer agentId) {
        return agentChargeMapper.selectByAgentThreeId(agentId);
    }
}
